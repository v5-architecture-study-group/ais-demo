package com.example.demo.ais.domain.primitives;

import java.util.Objects;

/**
 * Compass heading in degrees. 0 = true north, 90 = true east, 180 = true south, 270 = true west
 */
public final class Heading {

    private static final int UNAVAILABLE_HEADING = 511;
    public static final Heading UNAVAILABLE = new Heading(UNAVAILABLE_HEADING);
    private final int heading;

    private Heading(int heading) {
        if (heading < 0 || (heading > 360 && heading != UNAVAILABLE_HEADING)) {
            throw new IllegalArgumentException("Heading must be between 0° and 360°");
        }
        this.heading = heading == 360 ? 0 : heading;
    }

    public static Heading ofDegrees(int heading) {
        if (heading == UNAVAILABLE_HEADING) {
            return Heading.UNAVAILABLE;
        } else {
            return new Heading(heading);
        }
    }

    public int value() {
        return heading;
    }

    public boolean isUnavailable() {
        return heading == UNAVAILABLE_HEADING;
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
