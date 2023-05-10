package com.example.demo.ais.domain.primitives;

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
        if (!hasAsciiDigitsOnly(mmsi)) {
            throw new IllegalArgumentException("MMSI must consist of numbers only");
        }
        this.mmsi = mmsi;
    }

    private static boolean hasAsciiDigitsOnly(String s) {
        return s.chars().allMatch(MMSI::isAsciiDigit);
    }

    private static boolean isAsciiDigit(int c) {
        return (c >= '0') && (c <= '9');
    }
}
