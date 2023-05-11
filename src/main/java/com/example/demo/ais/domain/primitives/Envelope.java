package com.example.demo.ais.domain.primitives;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class Envelope {

    private final Latitude north;
    private final Latitude south;
    private final Longitude west;
    private final Longitude east;

    public Envelope(Latitude latitude1, Longitude longitude1, Latitude latitude2, Longitude longitude2) {
        requireNonNull(latitude1, "latitude1 must not be null");
        requireNonNull(longitude1, "longitude1 must not be null");
        requireNonNull(latitude2, "latitude2 must not be null");
        requireNonNull(longitude2, "longitude2 must not be null");

        if (latitude1.compareTo(latitude2) > 0) {
            north = latitude1;
            south = latitude2;
        } else {
            north = latitude2;
            south = latitude1;
        }

        if (longitude1.compareTo(longitude2) > 0) {
            east = longitude1;
            west = longitude2;
        } else {
            east = longitude2;
            west = longitude1;
        }
    }

    public boolean contains(Position position) {
        if (west.compareTo(position.longitude()) > 0 || east.compareTo(position.longitude()) < 0) {
            return false;
        }
        return west.compareTo(position.longitude()) <= 0
                && east.compareTo(position.longitude()) >= 0
                && south.compareTo(position.latitude()) <= 0
                && north.compareTo(position.latitude()) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Envelope envelope = (Envelope) o;
        return Objects.equals(north, envelope.north)
                && Objects.equals(south, envelope.south)
                && Objects.equals(west, envelope.west)
                && Objects.equals(east, envelope.east);
    }

    @Override
    public int hashCode() {
        return Objects.hash(north, south, west, east);
    }
}
