package com.example.demo.ais.util;

public final class StringUtils {

    private StringUtils() {
    }

    public static boolean hasAsciiDigitsOnly(String s) {
        return s.chars().allMatch(StringUtils::isAsciiDigit);
    }

    public static boolean hasAsciiDigitsOrLettersOnly(String s) {
        return s.chars().allMatch(StringUtils::isAsciiDigitOrLetter);
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
}
