package com.example.demo.ais.domain.primitives;

import java.util.Objects;

public final class CourseOverGround {

    private static final int UNAVAILABLE = 3600;

    private final int cog;

    private CourseOverGround(int cog) {
        if (cog < 0 || cog > UNAVAILABLE) {
            throw new IllegalArgumentException("COG must bet between 0 and 3600");
        }
        this.cog = cog;
    }

    public static CourseOverGround ofDegrees(double degrees) {
        return new CourseOverGround((int) (degrees * 10));
    }

    public static CourseOverGround ofDegreeTenths(int degreeTenths) {
        return new CourseOverGround(degreeTenths);
    }

    public boolean isUnavailable() {
        return cog == UNAVAILABLE;
    }

    public int degreeTenths() {
        return isUnavailable() ? -1 : cog;
    }

    public double degrees() {
        return isUnavailable() ? -1 : cog / 10.0;
    }

    public Heading asHeading() {
        return isUnavailable() ? Heading.UNAVAILABLE : Heading.ofDegrees(cog / 10);
    }

    public double radians() {
        return isUnavailable() ? -1 : cog * Math.PI / 1800;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseOverGround that = (CourseOverGround) o;
        return cog == that.cog;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cog);
    }
}
