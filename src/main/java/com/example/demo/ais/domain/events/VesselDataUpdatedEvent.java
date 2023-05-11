package com.example.demo.ais.domain.events;

import com.example.demo.ais.domain.data.VesselData;

import static java.util.Objects.requireNonNull;

public final class VesselDataUpdatedEvent extends VesselEvent {

    private final VesselData vesselData;

    public VesselDataUpdatedEvent(VesselData vesselData) {
        super(requireNonNull(vesselData, "vesselData must not be null").mmsi(), vesselData.timestamp());
        this.vesselData = vesselData;
    }

    public VesselData vesselData() {
        return vesselData;
    }
}
