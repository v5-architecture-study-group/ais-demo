package com.example.demo.ais.domain.primitives;

import com.example.demo.ais.util.StringUtils;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class VesselName {

    private final String vesselName;

    public VesselName(String vesselName) {
        requireNonNull(vesselName, "vesselName must not be null");
        var trimmedVesselName = vesselName.strip();

        if (trimmedVesselName.length() > 50) {
            throw new IllegalArgumentException("vesselName must not be longer than 50 characters");
        }
        this.vesselName = sanitizeVesselName(trimmedVesselName);
    }

    /**
     * The vessel names can contain all kinds of junk.
     */
    private static String sanitizeVesselName(String s) {
        return StringUtils.stripMatching(s, codePoint -> !isAllowedCharacter(codePoint));
    }

    private static boolean isAllowedCharacter(int codePoint) {
        return Character.isLetterOrDigit(codePoint)
                || Character.isWhitespace(codePoint)
                || codePoint == '-'
                || codePoint == '_'
                || codePoint == '/'
                || codePoint == '.'
                || codePoint == '\'';
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
