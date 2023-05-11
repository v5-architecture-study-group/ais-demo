package com.example.demo.ais.util;

import java.util.function.IntPredicate;

public final class StringUtils {

    private StringUtils() {
    }

    public static boolean hasAsciiDigitsOnly(String s) {
        return s.chars().allMatch(StringUtils::isAsciiDigit);
    }

    public static boolean isAsciiDigit(int c) {
        return (c >= '0') && (c <= '9');
    }

    public static boolean isAsciiLetter(int c) {
        return ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'));
    }

    public static boolean isAsciiDigitOrLetter(int c) {
        return isAsciiDigit(c) || isAsciiLetter(c);
    }

    public static String stripMatching(String s, IntPredicate predicate) {
        var sb = new StringBuilder();
        s.chars().filter(i -> !predicate.test(i)).forEach(sb::appendCodePoint);
        return sb.toString();
    }
}
