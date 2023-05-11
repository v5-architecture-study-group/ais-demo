package com.example.demo.ais.domain.primitives;

import com.example.demo.ais.util.StringUtils;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Maritime Mobile Service Identity
 */
public final class MMSI {

    private final String mmsi;

    public MMSI(String mmsi) {
        requireNonNull(mmsi, "MMSI must not be null");
        if (mmsi.length() != 9) {
            throw new IllegalArgumentException("MMSI must consist of exactly 9 characters");
        }
        if (!StringUtils.hasAsciiDigitsOnly(mmsi)) {
            throw new IllegalArgumentException("MMSI must consist of numbers only");
        }
        this.mmsi = mmsi;
    }

    public String value() {
        return mmsi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MMSI mmsi1 = (MMSI) o;
        return Objects.equals(mmsi, mmsi1.mmsi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mmsi);
    }
}
