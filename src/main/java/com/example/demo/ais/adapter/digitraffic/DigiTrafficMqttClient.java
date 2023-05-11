package com.example.demo.ais.adapter.digitraffic;

import com.example.demo.ais.domain.data.VesselData;
import com.example.demo.ais.domain.data.VesselLocation;
import com.example.demo.ais.domain.events.VesselDataUpdatedEvent;
import com.example.demo.ais.domain.events.VesselEvent;
import com.example.demo.ais.domain.events.VesselLocationUpdatedEvent;
import com.example.demo.ais.domain.primitives.*;
import com.example.demo.ais.util.Result;
import com.example.demo.ais.util.SubscriberList;
import com.example.demo.ais.util.Subscription;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.example.demo.ais.adapter.digitraffic.Constants.*;

class DigiTrafficMqttClient {

    private static final Logger log = LoggerFactory.getLogger(DigiTrafficMqttClient.class);
    private static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration RETRY_INTERVAL = Duration.ofSeconds(60);
    private final ScheduledExecutorService mqttReconnectionThread;
    private final ExecutorService subscriberNotificationThread;
    private final SubscriberList<Consumer<VesselEvent>> vesselEventSubscribers = new SubscriberList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final IMqttClient mqttClient;

    DigiTrafficMqttClient(
            ScheduledExecutorService mqttReconnectionThread,
            ExecutorService subscriberNotificationThread) {
        this.mqttReconnectionThread = mqttReconnectionThread;
        this.subscriberNotificationThread = subscriberNotificationThread;
        try {
            mqttClient = new MqttClient(MQTT_URL, APPLICATION_NAME);
        } catch (MqttException ex) {
            throw new IllegalStateException("Could not create MqttClient", ex);
        }
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                scheduleConnectToMqtt();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                // NOP
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // NOP
            }
        });
        tryConnectToMqtt();
    }

    void destroy() {
        try {
            if (mqttClient.isConnected()) {
                log.info("Disconnecting from MQTT");
                mqttClient.disconnect();
            }
        } catch (MqttException ex) {
            log.error("Error disconnecting from MQTT", ex);
        }
    }

    Subscription subscribeToVesselEvents(Consumer<VesselEvent> subscriber) {
        return vesselEventSubscribers.subscribe(subscriber);
    }

    private void tryConnectToMqtt() {
        try {
            var options = new MqttConnectOptions();
            options.setAutomaticReconnect(false); // We're handling this manually to be able to re-subscribe
            options.setCleanSession(true);
            options.setConnectionTimeout((int) CONNECTION_TIMEOUT.toSeconds());

            log.info("Trying to connect to MQTT");
            mqttClient.connect();

            log.info("Connected to MQTT, subscribing to topics");
            mqttClient.subscribe(ALL_LOCATIONS_TOPIC, 0, this::onVesselLocationChangeMessage);
            mqttClient.subscribe(ALL_VESSELS_TOPIC, 0, this::onVesselMetadataChangeMessage);
            log.info("Ready to receive MQTT messages");
        } catch (Throwable ex) {
            log.warn("Error connecting to MQTT", ex);
            scheduleConnectToMqtt();
        }
    }

    private void scheduleConnectToMqtt() {
        log.info("Will try to connect to MQ in {} seconds", RETRY_INTERVAL.toSeconds());
        mqttReconnectionThread.schedule(this::tryConnectToMqtt, RETRY_INTERVAL.toMillis(), TimeUnit.MILLISECONDS);
    }

    private void onVesselLocationChangeMessage(String topic, MqttMessage message) {
        convertToVesselLocation(topic, message)
                .doOnError(reason -> log.error("Error processing vessel location change message: {}", reason))
                .doIfSuccessful(this::notifySubscribersOfVesselLocationChange);
    }

    private Result<VesselLocation> convertToVesselLocation(String topic, MqttMessage message) {
        try {
            VesselLocationMessage vlm = objectMapper.readerFor(VesselLocationMessage.class).readValue(message.getPayload());
            var mmsi = extractMMSIFromTopicName(topic);
            var timestamp = Instant.ofEpochMilli(vlm.time());
            var lon = new Longitude(vlm.lon());
            var lat = new Latitude(vlm.lat());
            var heading = new Heading(vlm.heading());
            var position = vlm.posAcc() ? new AccuratePosition(lat, lon) : new InaccuratePosition(lat, lon);
            return Result.success(new VesselLocation(timestamp, mmsi, position, heading));
        } catch (Throwable ex) {
            return Result.failure(ex);
        }
    }

    private void notifySubscribersOfVesselLocationChange(VesselLocation vesselLocation) {
        notifySubscribersOfVesselEvent(new VesselLocationUpdatedEvent(vesselLocation));
    }

    private void onVesselMetadataChangeMessage(String topic, MqttMessage message) {
        convertToVesselData(topic, message)
                .doOnError(reason -> log.error("Error processing vessel metadata change message: {}", reason))
                .doIfSuccessful(this::notifySubscribersOfVesselDataChange);
    }

    private Result<VesselData> convertToVesselData(String topic, MqttMessage message) {
        try {
            VesselMetadataMessage vmm = objectMapper.readerFor(VesselMetadataMessage.class).readValue(message.getPayload());
            var mmsi = extractMMSIFromTopicName(topic);
            var timestamp = Instant.ofEpochMilli(vmm.timestamp());
            var vesselName = new VesselName(vmm.name());
            var callSign = new CallSign(vmm.callSign());
            var shipType = new ShipType(vmm.type());
            return Result.success(new VesselData(timestamp, mmsi, vesselName, callSign, shipType));
        } catch (Throwable ex) {
            return Result.failure(ex);
        }
    }

    private void notifySubscribersOfVesselDataChange(VesselData vesselData) {
        notifySubscribersOfVesselEvent(new VesselDataUpdatedEvent(vesselData));
    }

    private void notifySubscribersOfVesselEvent(VesselEvent event) {
        try {
            subscriberNotificationThread.submit(() -> vesselEventSubscribers.forEach(subscriber -> subscriber.accept(event)));
        } catch (RejectedExecutionException ex) {
            log.warn("The subscriber notification thread is receiving more events than it can handle");
        }
    }

    private MMSI extractMMSIFromTopicName(String topic) {
        // topic format is vessels-v2/<mmsi>/<suffix>
        var rawMMSI = topic.substring(11, 20); // MMSI:s are always exactly 9 digits
        return new MMSI(rawMMSI);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static
    private record VesselLocationMessage(
            long time,
            double sog,
            double cog,
            int navStat,
            int rot,
            boolean posAcc,
            boolean raim,
            int heading,
            double lon,
            double lat
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record VesselMetadataMessage(
            long timestamp,
            String destination,
            String name,
            int draught,
            long eta,
            int posType,
            int refA,
            int refB,
            int refC,
            int refD,
            String callSign,
            int imo,
            int type
    ) {
    }
}
