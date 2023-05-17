package com.example.demo.ais.adapter.ui;

import com.example.demo.ais.domain.primitives.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

final class Formatters {

    private Formatters() {
    }

    public static String formatInstant(Instant instant, ZoneId timeZone, Locale locale) {
        return ZonedDateTime
                .ofInstant(instant, timeZone)
                .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).withLocale(locale));
    }

    public static String formatLatitude(Latitude latitude, Locale locale) {
        // TODO Use the locale to get a localized version
        var dd = latitude.value();
        var h = dd < 0 ? 'S' : 'N';
        var degrees = (int) Math.abs(dd);
        var minutes = (Math.abs(dd) - degrees) * 60;
        return String.format(locale, "%s %02d° %.3f'", h, degrees, minutes);
    }

    public static String formatLongitude(Longitude longitude, Locale locale) {
        // TODO Use the locale to get a localized version
        var dd = longitude.value();
        var h = dd < 0 ? 'W' : 'E';
        var degrees = (int) Math.abs(dd);
        var minutes = (Math.abs(dd) - degrees) * 60;
        return String.format(locale, "%s %03d° %.3f'", h, degrees, minutes);
    }

    public static String formatHeading(Heading heading, Locale locale) {
        if (heading.isUnavailable()) {
            return "Unavailable"; // TODO Use the locale to get a localized version
        }
        return "%03d°".formatted(heading.degrees());
    }

    public static String formatCOG(CourseOverGround cog, Locale locale) {
        if (cog.isUnavailable()) {
            return "Unavailable"; // TODO Use the locale to get a localized version
        }
        return "%03.1f°".formatted(cog.degrees());
    }

    public static String formatSOG(SpeedOverGround sog, Locale locale) {
        if (sog.isUnavailable()) {
            return "Unavailable"; // TODO Use the locale to get a localized version
        }
        return "%.1f knots (%.1f km/h)".formatted(sog.knots(), sog.kilometersPerHour());
    }

    public static String formatPositionAccuracy(Position position, Locale locale) {
        return switch (position) {
            // TODO Use the locale to get a localized version
            case AccuratePosition ap -> "Accurate";
            case InaccuratePosition ip -> "Inaccurate";
        };
    }
}
