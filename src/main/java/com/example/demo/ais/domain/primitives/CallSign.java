package com.example.demo.ais.domain.primitives;

import com.example.demo.ais.util.StringUtils;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class CallSign {

    private final String callSign;

    public CallSign(String callSign) {
        requireNonNull(callSign, "callSign must not be null");
        var trimmedCallSign = callSign.strip().toUpperCase();

        if (trimmedCallSign.length() > 10) { // In practice, most call signs are 4 or 5 characters, but I'm not sure if they can't be longer
            throw new IllegalArgumentException("callSign must not be longer than 10 characters");
        }
        this.callSign = sanitizeCallSign(trimmedCallSign);
    }

    /**
     * Call signs should only contain numbers and ASCII letters but because they can be entered into the system
     * with all kinds of separator characters, we just remove them.
     */
    private static String sanitizeCallSign(String s) {
        return StringUtils.stripMatching(s, codePoint -> !StringUtils.isAsciiDigitOrLetter(codePoint));
    }

    public String value() {
        return callSign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallSign callSign1 = (CallSign) o;
        return Objects.equals(callSign, callSign1.callSign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(callSign);
    }
}
