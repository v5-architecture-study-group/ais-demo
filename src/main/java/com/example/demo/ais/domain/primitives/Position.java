package com.example.demo.ais.domain.primitives;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public abstract sealed class Position permits AccuratePosition, InaccuratePosition {

    private final Latitude latitude;
    private final Longitude longitude;

    public Position(Latitude latitude, Longitude longitude) {
        this.latitude = requireNonNull(latitude, "latitude must not be null");
        this.longitude = requireNonNull(longitude, "longitude must not be null");
    }

    public Latitude latitude() {
        return latitude;
    }

    public Longitude longitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Objects.equals(latitude, position.latitude) && Objects.equals(longitude, position.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
