package com.example.demo.ais.adapter.ui;

import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;

import java.time.ZoneId;

@VaadinSessionScope
@SpringComponent
class UserPreferences {

    // TODO You could load and store this information in a database, so that the user get the same state back
    //  when starting a new session.
    private static final ZoneId DEFAULT_TIME_ZONE = ZoneId.systemDefault();
    private static final VesselMap.State DEFAULT_MAP_STATE = VesselMap.State.DEFAULT;

    private ZoneId timeZone = DEFAULT_TIME_ZONE;
    private VesselMap.State mapState = DEFAULT_MAP_STATE;

    public ZoneId timeZone() {
        return timeZone;
    }

    public void setTimeZone(ZoneId timeZone) {
        this.timeZone = timeZone == null ? DEFAULT_TIME_ZONE : timeZone;
    }

    public VesselMap.State mapState() {
        return mapState;
    }

    public void setMapState(VesselMap.State mapState) {
        this.mapState = mapState == null ? DEFAULT_MAP_STATE : mapState;
    }
}
