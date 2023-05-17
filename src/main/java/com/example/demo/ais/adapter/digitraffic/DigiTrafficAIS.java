package com.example.demo.ais.adapter.digitraffic;

import com.example.demo.ais.domain.data.VesselData;
import com.example.demo.ais.domain.data.VesselLocation;
import com.example.demo.ais.domain.events.VesselEvent;
import com.example.demo.ais.service.spi.AIS;
import com.example.demo.ais.util.Result;
import com.example.demo.ais.util.Subscription;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.function.Consumer;

class DigiTrafficAIS implements AIS {

    private static final Logger log = LoggerFactory.getLogger(DigiTrafficAIS.class);
    private static final int SUBSCRIBER_NOTIFICATION_JOB_QUEUE_CAPACITY = 1000;
    private final DigiTrafficRestClient restClient = new DigiTrafficRestClient();
    private final DigiTrafficMqttClient mqttClient;
    private final ScheduledExecutorService mqttReconnectionThread;
    private final ExecutorService subscriberNotificationThread;

    public DigiTrafficAIS(MeterRegistry meterRegistry, Clock clock) {
        log.info("Starting MQTT reconnection thread");
        mqttReconnectionThread = Executors.newSingleThreadScheduledExecutor();
        log.info("Starting subscriber notification thread");
        subscriberNotificationThread = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(SUBSCRIBER_NOTIFICATION_JOB_QUEUE_CAPACITY));
        mqttClient = new DigiTrafficMqttClient(mqttReconnectionThread, subscriberNotificationThread, meterRegistry, clock);
    }

    @PreDestroy
    void destroy() {
        mqttClient.destroy();
        log.info("Shutting down MQTT reconnection thread");
        mqttReconnectionThread.shutdown();
        log.info("Shutting down subscriber notification thread");
        subscriberNotificationThread.shutdown();
    }

    @Override
    public Result<Collection<VesselLocation>> loadAllVesselLocations() {
        return restClient.loadAllVesselLocations();
    }

    @Override
    public Result<Collection<VesselData>> loadAllVesselData() {
        return restClient.loadAllVesselData();
    }

    @Override
    public Subscription subscribeToVesselEvents(Consumer<VesselEvent> subscriber) {
        return mqttClient.subscribeToVesselEvents(subscriber);
    }
}
