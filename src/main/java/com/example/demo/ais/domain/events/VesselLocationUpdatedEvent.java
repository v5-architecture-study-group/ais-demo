package com.example.demo.ais.domain.events;

import com.example.demo.ais.domain.primitives.Heading;
import com.example.demo.ais.domain.primitives.MMSI;
import com.example.demo.ais.domain.primitives.Position;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

public final class VesselLocationUpdatedEvent extends VesselEvent {

    private final Instant locationTimestamp;
    private final Position position;
    private final Heading heading;

    public VesselLocationUpdatedEvent(MMSI mmsi, Instant eventTimestamp, Instant locationTimestamp, Position position, Heading heading) {
        super(mmsi, eventTimestamp);
        this.locationTimestamp = requireNonNull(locationTimestamp, "locationTimestamp must not be null");
        this.position = requireNonNull(position, "position must not be null");
        this.heading = requireNonNull(heading, "heading must not be null");
    }

    public Instant locationTimestamp() {
        return locationTimestamp;
    }

    public Position position() {
        return position;
    }

    public Heading heading() {
        return heading;
    }
}
