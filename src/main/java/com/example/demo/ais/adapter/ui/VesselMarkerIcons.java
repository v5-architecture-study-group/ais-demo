package com.example.demo.ais.adapter.ui;

import com.example.demo.ais.domain.primitives.Heading;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.map.configuration.style.Icon;

import java.util.HashMap;

final class VesselMarkerIcons {

    private static final HashMap<Heading, Icon> VESSEL_MARKER_ICONS = new HashMap<>();

    static {
        for (int i = 0; i < 360; ++i) {
            var iconOptions = new Icon.Options();
            iconOptions.setSrc("markers/vessel.svg");
            iconOptions.setRotation(i * Math.PI / 180);
            VESSEL_MARKER_ICONS.put(Heading.ofDegrees(i), new Icon(iconOptions));
        }
        var iconOptions = new Icon.Options();
        iconOptions.setSrc("markers/vessel-no-bearing.svg");
        VESSEL_MARKER_ICONS.put(Heading.UNAVAILABLE, new Icon(iconOptions));
    }

    private VesselMarkerIcons() {
    }

    public static Icon getIcon(Heading heading) {
        return VESSEL_MARKER_ICONS.getOrDefault(heading, MarkerFeature.POINT_ICON);
    }
}
