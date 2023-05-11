package com.example.demo.ais.domain.events;

import com.example.demo.ais.domain.primitives.MMSI;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

public abstract sealed class VesselEvent permits VesselLocationOutdatedEvent, VesselLocationUpdatedEvent, VesselDataUpdatedEvent {

    private final MMSI mmsi;
    private final Instant eventTimestamp;

    protected VesselEvent(MMSI mmsi, Instant eventTimestamp) {
        this.mmsi = requireNonNull(mmsi, "mmsi must not be null");
        this.eventTimestamp = requireNonNull(eventTimestamp, "eventTimestamp must not be null");
    }

    public MMSI mmsi() {
        return mmsi;
    }

    public Instant eventTimestamp() {
        return eventTimestamp;
    }
}
