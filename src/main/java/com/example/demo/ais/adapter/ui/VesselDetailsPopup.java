package com.example.demo.ais.adapter.ui;

import com.example.demo.ais.service.dpo.VesselDetails;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;

import java.time.ZoneId;

public class VesselDetailsPopup extends Dialog {

    private final UnorderedList attributes = new UnorderedList();

    VesselDetailsPopup(ZoneId timeZone, VesselDetails vesselDetails) {
        addClassName("vessel-details-popup");
        attributes.addClassName("vessel-attributes");
        add(attributes);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);
        setModal(true);

        addAttribute("MMSI", vesselDetails.mmsi().value());
        vesselDetails.vesselData().ifPresent(vesselData -> {
            addAttribute("Name", vesselData.vesselName().value());
            addAttribute("Call sign", vesselData.callSign().value());
            addAttribute("Ship type", vesselData.shipType().description());
            addAttribute("Data last updated", Formatters.formatInstant(vesselData.timestamp(), timeZone, getLocale()));
        });
        vesselDetails.vesselLocation().ifPresent(vesselLocation -> {
            addAttribute("Latitude", Formatters.formatLatitude(vesselLocation.position().latitude(), getLocale()));
            addAttribute("Longitude", Formatters.formatLongitude(vesselLocation.position().longitude(), getLocale()));
            addAttribute("Position quality", Formatters.formatPositionAccuracy(vesselLocation.position(), getLocale()));
            addAttribute("Heading", Formatters.formatHeading(vesselLocation.heading(), getLocale()));
            addAttribute("Location last updated", Formatters.formatInstant(vesselLocation.timestamp(), timeZone, getLocale()));
        });
    }

    private void addAttribute(String name, String value) {
        var keySpan = new Span(name);
        keySpan.addClassName("attribute-key");

        var valueSpan = new Span(value);
        valueSpan.addClassName("attribute-value");

        attributes.add(new ListItem(keySpan, valueSpan));
    }
}
