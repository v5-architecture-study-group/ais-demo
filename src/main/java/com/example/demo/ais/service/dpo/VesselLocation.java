package com.example.demo.ais.service.dpo;

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
) {

    public VesselLocation {
        requireNonNull(timestamp, "timestamp must not be null");
        requireNonNull(mmsi, "mmsi must not be null");
        requireNonNull(position, "position must not be null");
        requireNonNull(heading, "heading must not be null");
    }
}
