package com.example.demo.ais.domain.events;

import com.example.demo.ais.domain.primitives.MMSI;

import java.time.Instant;

public final class VesselLocationOutdatedEvent extends VesselEvent {
    public VesselLocationOutdatedEvent(MMSI mmsi, Instant eventTimestamp) {
        super(mmsi, eventTimestamp);
    }
}
