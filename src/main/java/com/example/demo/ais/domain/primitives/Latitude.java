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

    public Latitude plusWithoutWraparound(double degrees) {
        var newCoordinate = value() + degrees;
        if (newCoordinate < -90) {
            return new Latitude(-90);
        } else if (newCoordinate > 90) {
            return new Latitude(90);
        } else {
            return new Latitude(newCoordinate);
        }
    }

    public Latitude minusWithoutWraparound(double degrees) {
        return plusWithoutWraparound(-degrees);
    }
}
