package com.example.demo.ais.adapter.ui;

import com.example.demo.ais.domain.primitives.Heading;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.map.configuration.style.Icon;

import java.util.HashMap;

final class VesselMarkerIcons {

    private static final HashMap<Heading, Icon> MOVING_VESSEL_MARKER_ICONS = new HashMap<>();
    private static final HashMap<Heading, Icon> NON_MOVING_VESSEL_MARKER_ICONS = new HashMap<>();

    static {
        for (int i = 0; i < 360; ++i) {
            var h = Heading.ofDegrees(i);
            MOVING_VESSEL_MARKER_ICONS.put(h, createIcon("markers/vessel.svg", h));
            NON_MOVING_VESSEL_MARKER_ICONS.put(h, createIcon("markers/non-moving-vessel.svg", h));
        }
        MOVING_VESSEL_MARKER_ICONS.put(Heading.UNAVAILABLE, createIcon("markers/vessel-no-bearing.svg", Heading.UNAVAILABLE));
        NON_MOVING_VESSEL_MARKER_ICONS.put(Heading.UNAVAILABLE, createIcon("markers/non-moving-vessel-no-bearing.svg", Heading.UNAVAILABLE));
    }

    private static Icon createIcon(String src, Heading heading) {
        var iconOptions = new Icon.Options();
        iconOptions.setSrc(src);
        if (!heading.isUnavailable()) {
            iconOptions.setRotation(heading.radians());
        }
        return new Icon(iconOptions);
    }

    private VesselMarkerIcons() {
    }

    public static Icon getMovingVesselIcon(Heading heading) {
        return MOVING_VESSEL_MARKER_ICONS.getOrDefault(heading, MarkerFeature.POINT_ICON);
    }

    public static Icon getNonMovingVesselIcon(Heading heading) {
        return NON_MOVING_VESSEL_MARKER_ICONS.getOrDefault(heading, MarkerFeature.POINT_ICON);
    }
}
