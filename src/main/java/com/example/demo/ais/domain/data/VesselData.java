package com.example.demo.ais.domain.data;

import com.example.demo.ais.domain.base.Identifiable;
import com.example.demo.ais.domain.primitives.CallSign;
import com.example.demo.ais.domain.primitives.MMSI;
import com.example.demo.ais.domain.primitives.ShipType;
import com.example.demo.ais.domain.primitives.VesselName;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

public record VesselData(
        Instant timestamp,
        MMSI mmsi,
        VesselName vesselName,
        CallSign callSign,
        ShipType shipType
) implements Identifiable<MMSI> {

    public VesselData {
        requireNonNull(timestamp, "timestamp must not be null");
        requireNonNull(mmsi, "mmsi must not be null");
        requireNonNull(vesselName, "vesselName must not be null");
        requireNonNull(callSign, "callSign must not be null");
        requireNonNull(shipType, "shipType must not be null");
    }

    @Override
    public MMSI id() {
        return mmsi();
    }
}
