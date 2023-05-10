package com.example.demo.ais.adapter.digitraffic;

import com.example.demo.ais.domain.primitives.*;
import com.example.demo.ais.service.dpo.VesselLocation;
import com.example.demo.ais.service.spi.AIS;
import com.example.demo.ais.util.Result;
import com.example.demo.ais.util.SubscriberList;
import com.example.demo.ais.util.Subscription;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@Component
class DigiTrafficAIS implements AIS {

    private static final String APPLICATION_NAME = "Vaadin.com / PH / AIS Demo App";
    private static final Logger log = LoggerFactory.getLogger(DigiTrafficAIS.class);
    private static final String VESSEL_LOCATIONS_URL = "https://meri.digitraffic.fi/api/ais/v1/locations";
    private static final String MQTT_URL = "wss://meri.digitraffic.fi:443/mqtt";
    private static final String ALL_LOCATIONS_TOPIC = "vessels-v2/+/location";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final IMqttClient mqttClient;
    private final ScheduledExecutorService mqttReconnectionThread;
    private final ExecutorService subscriberNotificationThread;
    private final SubscriberList<Consumer<VesselLocation>> vesselLocationChangeSubscribers = new SubscriberList<>();

    public DigiTrafficAIS() throws MqttException {
        restTemplate = new RestTemplate();
        objectMapper = new ObjectMapper();
        mqttReconnectionThread = Executors.newSingleThreadScheduledExecutor();
        subscriberNotificationThread = Executors.newSingleThreadExecutor();
        mqttClient = new MqttClient(MQTT_URL, APPLICATION_NAME);
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                scheduleConnectToMqtt();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // NOP
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // NOP
            }
        });
        tryConnectToMqtt();
    }

    private void tryConnectToMqtt() {
        try {
            var options = new MqttConnectOptions();
            options.setAutomaticReconnect(false); // We're handling this manually to be able to re-subscribe
            options.setCleanSession(true);
            options.setConnectionTimeout(10); // Seconds

            log.info("Trying to connect to MQTT");
            mqttClient.connect();

            log.info("Connected to MQTT, subscribing to topics");
            mqttClient.subscribe(ALL_LOCATIONS_TOPIC, 0, this::onVesselLocationChangeMessage);
        } catch (Exception ex) {
            log.warn("Error connecting to MQTT", ex);
            scheduleConnectToMqtt();
        }
    }

    private void scheduleConnectToMqtt() {
        log.info("Will try to connect to MQ in 1 minute");
        mqttReconnectionThread.schedule(this::tryConnectToMqtt, 1, TimeUnit.MINUTES);
    }

    @PreDestroy
    void destroy() throws MqttException {
        try {
            if (mqttClient.isConnected()) {
                log.info("Disconnecting from MQTT");
                mqttClient.disconnect();
            }
        } finally {
            mqttReconnectionThread.shutdown();
            subscriberNotificationThread.shutdown();
        }
    }

    @Override
    public Result<Collection<VesselLocation>> findAllVesselLocations() {
        return doFindVesselLocations().map(this::featureCollectionToVesselLocations);
    }

    private Result<FeatureCollection> doFindVesselLocations() {
        final var httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.ACCEPT_ENCODING, "gzip");
        httpHeaders.set("Digitraffic-User", APPLICATION_NAME);

        try {
            var response = restTemplate.exchange(
                    VESSEL_LOCATIONS_URL,
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    Resource.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                return Result.failure("Server responded with error %d".formatted(response.getStatusCode().value()));
            }

            if (!response.hasBody()) {
                return Result.failure("Response had no body");
            }

            var contentEncoding = response.getHeaders().getFirst(HttpHeaders.CONTENT_ENCODING);
            if (!"gzip".equalsIgnoreCase(contentEncoding)) {
                return Result.failure("Unknown content encoding");
            }

            try (GZIPInputStream gzipInputStream = new GZIPInputStream(response.getBody().getInputStream())) {
                return Result.success(objectMapper.readerFor(FeatureCollection.class).readValue(gzipInputStream));
            }
        } catch (IOException | RestClientException ex) {
            return Result.failure(ex.getMessage());
        }
    }

    private Collection<VesselLocation> featureCollectionToVesselLocations(FeatureCollection featureCollection) {
        return Arrays.stream(featureCollection.features())
                .map(this::featureToVesselLocation)
                .filter(Result::isSuccessful)
                .map(Result::get)
                .collect(Collectors.toList());
    }

    private Result<VesselLocation> featureToVesselLocation(Feature feature) {
        try {
            var timestamp = Instant.ofEpochMilli(feature.properties().timestampExternal());
            var mmsi = new MMSI(String.valueOf(feature.mmsi()));
            if (!"point".equalsIgnoreCase(feature.geometry().type())) {
                return Result.failure("Unknown geometry");
            }
            var lon = new Longitude(feature.geometry().coordinates()[0]);
            var lat = new Latitude(feature.geometry().coordinates()[1]);
            var heading = new Heading(feature.properties().heading());
            var position = feature.properties().posAcc() ? new AccuratePosition(lat, lon) : new InaccuratePosition(lat, lon);
            return Result.success(new VesselLocation(timestamp, mmsi, position, heading));
        } catch (Exception ex) {
            return Result.failure(ex);
        }
    }

    private void onVesselLocationChangeMessage(String topic, MqttMessage message) {
        mqttMessageToVesselLocation(topic, message).doIfSuccessful(this::notifySubscribersOfVesselLocationChange);
    }

    private Result<VesselLocation> mqttMessageToVesselLocation(String topic, MqttMessage message) {
        try {
            VesselLocationMessage vlm = objectMapper.readerFor(VesselLocationMessage.class).readValue(message.getPayload());
            var mmsi = extractMMSIFromTopicName(topic);
            var timestamp = Instant.ofEpochMilli(vlm.time());
            var lon = new Longitude(vlm.lon());
            var lat = new Latitude(vlm.lat());
            var heading = new Heading(vlm.heading());
            var position = vlm.posAcc() ? new AccuratePosition(lat, lon) : new InaccuratePosition(lat, lon);
            return Result.success(new VesselLocation(timestamp, mmsi, position, heading));
        } catch (Exception ex) {
            return Result.failure(ex);
        }
    }

    private MMSI extractMMSIFromTopicName(String topic) {
        // topic format is vessels-v2/<mmsi>/<suffix>
        var rawMMSI = topic.substring(11, 20); // MMSI:s are always exactly 9 digits
        return new MMSI(rawMMSI);
    }

    private void notifySubscribersOfVesselLocationChange(VesselLocation newVesselLocation) {
        subscriberNotificationThread.submit(() -> vesselLocationChangeSubscribers.visit(subscriber -> subscriber.accept(newVesselLocation)));
    }

    @Override
    public Subscription subscribeToVesselLocationChanges(Consumer<VesselLocation> subscriber) {
        return vesselLocationChangeSubscribers.subscribe(subscriber);
    }
}
