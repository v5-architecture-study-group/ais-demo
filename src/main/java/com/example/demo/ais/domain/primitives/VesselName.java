package com.example.demo.ais.domain.primitives;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class VesselName {

    private final String vesselName;

    public VesselName(String vesselName) {
        requireNonNull(vesselName, "vesselName must not be null");
        if (vesselName.length() > 50) {
            throw new IllegalArgumentException("vesselName must not be longer than 50 characters");
        }
        if (!hasAllowedCharactersOnly(vesselName)) {
            throw new IllegalArgumentException("vesselName contains illegal characters");
        }
        this.vesselName = vesselName;
    }

    private static boolean hasAllowedCharactersOnly(String s) {
        return s.chars().allMatch(codePoint ->
                Character.isLetterOrDigit(codePoint)
                        || Character.isWhitespace(codePoint)
                        || codePoint == '-'
                        || codePoint == '/'
                        || codePoint == '.'
        );
    }

    public String value() {
        return vesselName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VesselName that = (VesselName) o;
        return Objects.equals(vesselName, that.vesselName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vesselName);
    }
}
