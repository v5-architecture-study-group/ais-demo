package com.example.demo.ais.domain.data;

import com.example.demo.ais.domain.base.Identifiable;
import com.example.demo.ais.domain.primitives.Heading;
import com.example.demo.ais.domain.primitives.MMSI;
import com.example.demo.ais.domain.primitives.Position;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

public record VesselLocation(
        Instant timestamp,
        MMSI mmsi,
        Position position,
        Heading heading
) implements Identifiable<MMSI> {

    public VesselLocation {
        requireNonNull(timestamp, "timestamp must not be null");
        requireNonNull(mmsi, "mmsi must not be null");
        requireNonNull(position, "position must not be null");
        requireNonNull(heading, "heading must not be null");
    }

    @Override
    public MMSI id() {
        return mmsi();
    }
}
