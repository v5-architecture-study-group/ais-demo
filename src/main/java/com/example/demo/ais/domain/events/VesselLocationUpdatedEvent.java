package com.example.demo.ais.domain.events;

import com.example.demo.ais.domain.data.VesselLocation;

import static java.util.Objects.requireNonNull;

public final class VesselLocationUpdatedEvent extends VesselEvent {

    private final VesselLocation vesselLocation;

    public VesselLocationUpdatedEvent(VesselLocation vesselLocation) {
        super(requireNonNull(vesselLocation, "vesselLocation must not be null").mmsi(),
                vesselLocation.timestamp());
        this.vesselLocation = vesselLocation;
    }

    public VesselLocation vesselLocation() {
        return vesselLocation;
    }
}
