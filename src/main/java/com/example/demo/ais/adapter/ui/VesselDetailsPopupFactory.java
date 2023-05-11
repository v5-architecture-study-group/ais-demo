package com.example.demo.ais.adapter.ui;

import com.example.demo.ais.service.dpo.VesselDetails;
import com.vaadin.flow.spring.annotation.SpringComponent;

import java.time.ZoneId;

@SpringComponent
class VesselDetailsPopupFactory {

    public VesselDetailsPopup create(VesselDetails vesselDetails) {
        // TODO Fetch time zone from user preferences
        return new VesselDetailsPopup(ZoneId.systemDefault(), vesselDetails);
    }
}
