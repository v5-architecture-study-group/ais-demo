package com.example.demo.ais.adapter.ui;

import com.example.demo.ais.service.dpo.VesselDetails;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.ObjectFactory;

@SpringComponent
class VesselDetailsPopupFactory {

    private final ObjectFactory<UserPreferences> userPreferencesObjectFactory;

    VesselDetailsPopupFactory(ObjectFactory<UserPreferences> userPreferencesObjectFactory) {
        this.userPreferencesObjectFactory = userPreferencesObjectFactory;
    }

    public VesselDetailsPopup create(VesselDetails vesselDetails) {
        var userPreferences = userPreferencesObjectFactory.getObject();
        return new VesselDetailsPopup(userPreferences.timeZone(), vesselDetails);
    }
}
