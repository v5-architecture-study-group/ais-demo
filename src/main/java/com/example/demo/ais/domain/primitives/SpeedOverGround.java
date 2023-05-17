package com.example.demo.ais.domain.primitives;

import java.util.Objects;

public final class SpeedOverGround {

    private static final int UNAVAILABLE = 1023;

    private final int sog;

    private SpeedOverGround(int sog) {
        if (sog < 0 || sog > UNAVAILABLE) {
            throw new IllegalArgumentException("SOG must be between 0 and 1023");
        }
        this.sog = sog;
    }

    public static SpeedOverGround ofKnots(double knots) {
        return new SpeedOverGround((int) (Math.min(knots * 10, 1022))); // 1022 means 102,2 knots OR FASTER
    }

    /**
     * One knot step = 1/10 of a knot
     */
    public static SpeedOverGround ofKnotTenths(int knotTenths) {
        return new SpeedOverGround(Math.min(knotTenths, 1022)); // 1022 means 102.2 knots OR FASTER
    }

    public boolean isUnavailable() {
        return sog == UNAVAILABLE;
    }

    public int knotTenths() {
        return isUnavailable() ? -1 : sog;
    }

    public double knots() {
        return isUnavailable() ? -1 : sog / 10.0;
    }

    public double kilometersPerHour() {
        return isUnavailable() ? -1 : sog * 0.1852;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpeedOverGround that = (SpeedOverGround) o;
        return sog == that.sog;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sog);
    }
}
