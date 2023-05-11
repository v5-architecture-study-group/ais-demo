package com.example.demo.ais.domain.events;

import com.example.demo.ais.domain.primitives.MMSI;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

public abstract sealed class VesselEvent permits VesselLocationOutdatedEvent, VesselLocationUpdatedEvent, VesselDataUpdatedEvent {

    private final MMSI mmsi;
    private final Instant timestamp;

    protected VesselEvent(MMSI mmsi, Instant timestamp) {
        this.mmsi = requireNonNull(mmsi, "mmsi must not be null");
        this.timestamp = requireNonNull(timestamp, "timestamp must not be null");
    }

    public MMSI mmsi() {
        return mmsi;
    }

    public Instant timestamp() {
        return timestamp;
    }
}
