package com.example.demo.ais.domain.events;

import com.example.demo.ais.domain.primitives.CallSign;
import com.example.demo.ais.domain.primitives.MMSI;
import com.example.demo.ais.domain.primitives.ShipType;
import com.example.demo.ais.domain.primitives.VesselName;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

public final class VesselDataUpdatedEvent extends VesselEvent {

    private final Instant updateTimestamp;
    private final VesselName vesselName;
    private final CallSign callSign;
    private final ShipType shipType;

    public VesselDataUpdatedEvent(MMSI mmsi, Instant eventTimestamp, Instant updateTimestamp, VesselName vesselName, CallSign callSign, ShipType shipType) {
        super(mmsi, eventTimestamp);
        this.updateTimestamp = requireNonNull(updateTimestamp, "updateTimestamp must not be null");
        this.vesselName = requireNonNull(vesselName, "vesselName must not be null");
        this.callSign = requireNonNull(callSign, "callSign must not be null");
        this.shipType = requireNonNull(shipType, "shipType must not be null");
    }

    public Instant updateTimestamp() {
        return updateTimestamp;
    }

    public VesselName vesselName() {
        return vesselName;
    }

    public CallSign callSign() {
        return callSign;
    }

    public ShipType shipType() {
        return shipType;
    }
}
