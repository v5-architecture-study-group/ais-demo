package com.example.demo.ais.domain.primitives;

/**
 * WGS-84 longitude coordinate.
 */
public final class Longitude extends Coordinate {

    public Longitude(double coordinate) {
        super(coordinate);
    }

    @Override
    protected double validateCoordinate(double coordinate) {
        if (coordinate < -180 || coordinate > 180) {
            throw new IllegalArgumentException("Longitude must be between -180° and 180°");
        }
        return coordinate;
    }
}
