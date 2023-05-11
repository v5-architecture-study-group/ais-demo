package com.example.demo.ais.domain.primitives;

/**
 * WGS-84 latitude coordinate.
 */
public final class Latitude extends Coordinate implements Comparable<Latitude> {

    public Latitude(double coordinate) {
        super(coordinate);
    }

    @Override
    protected double validateCoordinate(double coordinate) {
        if (coordinate < -90 || coordinate > 90) {
            throw new IllegalArgumentException("Latitude must be between -90° and 90°");
        }
        return coordinate;
    }

    @Override
    public int compareTo(Latitude o) {
        return Double.compare(value(), o.value());
    }
}
