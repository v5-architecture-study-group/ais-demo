package com.example.demo.ais.domain.primitives;

import java.util.Objects;

/**
 * Compass heading in degrees. 0 = true north, 90 = true east, 180 = true south, 270 = true west
 */
public final class Heading {

    private final int heading;

    public Heading(int heading) {
        if (heading < 0 || heading > 360) {
            throw new IllegalArgumentException("Heading must be between 0° and 360°"); // 0 and 360 are the same direction, but we support both
        }
        this.heading = heading;
    }

    public int value() {
        return heading;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Heading heading1 = (Heading) o;
        return heading == heading1.heading;
    }

    @Override
    public int hashCode() {
        return Objects.hash(heading);
    }
}
