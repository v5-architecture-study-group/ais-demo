package com.example.demo.ais.service.impl;

import com.example.demo.ais.domain.data.VesselData;
import com.example.demo.ais.domain.data.VesselLocation;
import com.example.demo.ais.domain.events.VesselDataUpdatedEvent;
import com.example.demo.ais.domain.events.VesselEvent;
import com.example.demo.ais.domain.events.VesselLocationOutdatedEvent;
import com.example.demo.ais.domain.events.VesselLocationUpdatedEvent;
import com.example.demo.ais.domain.primitives.Envelope;
import com.example.demo.ais.domain.primitives.MMSI;
import com.example.demo.ais.service.api.VesselService;
import com.example.demo.ais.service.spi.AIS;
import com.example.demo.ais.util.Subscription;
import com.example.demo.ais.util.TumblingWindowEventDispatcher;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

@Service
class VesselServiceImpl implements VesselService {

    private static final Duration VESSEL_LOCATION_MAX_AGE = Duration.ofDays(1);
    private static final Duration EVENT_WINDOW_SIZE = Duration.ofSeconds(1);
    private final Cache<MMSI, VesselLocation> vesselLocationCache;
    private final Cache<MMSI, VesselData> vesselDataCache;
    private final ScheduledExecutorService eventDispatcherThread;
    private final TumblingWindowEventDispatcher<VesselEvent> vesselEventDispatcher;
    private final Clock clock;

    VesselServiceImpl(AIS ais, Clock clock, MeterRegistry meterRegistry) {
        this.clock = clock;
        this.eventDispatcherThread = Executors.newSingleThreadScheduledExecutor();
        this.vesselEventDispatcher = new TumblingWindowEventDispatcher<>(eventDispatcherThread, EVENT_WINDOW_SIZE);
        this.vesselLocationCache = new Cache<>(ais.loadAllVesselLocations().orElse(Collections.emptySet()), this::isOutdated);
        this.vesselDataCache = new Cache<>(ais.loadAllVesselData().orElse(Collections.emptySet()));

        Gauge.builder("vessel-service.cache.vessel-location.size", this.vesselDataCache::size).register(meterRegistry);
        Gauge.builder("vessel-service.cache.vessel-data.size", this.vesselDataCache::size).register(meterRegistry);

        ais.subscribeToVesselEvents(this::onVesselEvent); // No need to unsubscribe; cache and service have the same scope
    }

    private boolean isOutdated(VesselLocation vesselLocation) {
        var now = clock.instant();
        return Duration.between(vesselLocation.timestamp(), now).compareTo(VESSEL_LOCATION_MAX_AGE) < 0;
    }

    @PreDestroy
    void destroy() {
        eventDispatcherThread.shutdown();
    }

    private void onVesselEvent(VesselEvent vesselEvent) {
        switch (vesselEvent) {
            case VesselDataUpdatedEvent due -> onVesselDataUpdatedEvent(due);
            case VesselLocationUpdatedEvent lue -> onVesselLocationUpdatedEvent(lue);
            case VesselLocationOutdatedEvent loe -> onVesselLocationOutdatedEvent(loe);
        }
    }

    private void onVesselDataUpdatedEvent(VesselDataUpdatedEvent event) {
        vesselDataCache.put(event.vesselData());
        vesselEventDispatcher.enqueue(event);
    }

    private void onVesselLocationUpdatedEvent(VesselLocationUpdatedEvent event) {
        var location = event.vesselLocation();
        if (isOutdated(location)) {
            onVesselLocationOutdatedEvent(new VesselLocationOutdatedEvent(location.mmsi(), clock.instant()));
        } else {
            vesselLocationCache.put(location);
            vesselEventDispatcher.enqueue(event);
        }
    }

    private void onVesselLocationOutdatedEvent(VesselLocationOutdatedEvent event) {
        vesselLocationCache.removeKey(event.mmsi());
        vesselEventDispatcher.enqueue(event);
    }

    @Override
    public Collection<VesselLocation> vesselLocations(Envelope envelope, int maxResultSize) {
        return vesselLocationCache.values()
                .filter(l -> envelope.contains(l.position()))
                .limit(maxResultSize)
                .toList();
    }

    @Override
    public Optional<VesselData> findVesselData(MMSI mmsi) {
        return vesselDataCache.get(mmsi);
    }

    @Override
    public Collection<VesselData> findVesselData(String searchTerm, int maxResultSize) {
        requireNonNull(searchTerm, "searchTerm must not be null");
        var sanitizedSearchTerm = searchTerm.toLowerCase().trim();
        if (sanitizedSearchTerm.length() < 3 || sanitizedSearchTerm.length() > 50) {
            return Collections.emptyList();
        }
        return vesselDataCache.values()
                .filter(searchTermMatches(sanitizedSearchTerm))
                .limit(maxResultSize)
                .toList();
    }

    private Predicate<VesselData> searchTermMatches(String searchTerm) {
        return vd ->
                vd.callSign().value().toLowerCase().startsWith(searchTerm)
                        || vd.vesselName().value().toLowerCase().startsWith(searchTerm)
                        || vd.mmsi().value().toLowerCase().startsWith(searchTerm);
    }

    @Override
    public Subscription subscribeToVesselEvents(Consumer<List<VesselEvent>> listener) {
        return vesselEventDispatcher.subscribe(listener);
    }
}
