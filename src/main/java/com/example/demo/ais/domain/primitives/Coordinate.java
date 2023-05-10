package com.example.demo.ais.domain.primitives;

import java.util.Objects;

public abstract sealed class Coordinate permits Latitude, Longitude {

    private final double coordinate;

    public Coordinate(double coordinate) {
        this.coordinate = validateCoordinate(coordinate);
    }

    protected abstract double validateCoordinate(double coordinate);

    public double value() {
        return coordinate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return Double.compare(that.coordinate, coordinate) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinate);
    }
}
