package com.example.demo.ais.adapter.ui;

import com.example.demo.ais.domain.events.VesselEvent;
import com.example.demo.ais.domain.events.VesselLocationOutdatedEvent;
import com.example.demo.ais.domain.events.VesselLocationUpdatedEvent;
import com.example.demo.ais.domain.primitives.Envelope;
import com.example.demo.ais.domain.primitives.MMSI;
import com.example.demo.ais.service.api.VesselService;
import com.example.demo.ais.util.Subscription;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("")
public class MapRoute extends VerticalLayout {

    private static final int MAX_VESSELS_ON_MAP = 15000;
    private final VesselService vesselService;
    private final VesselDetailsPopupFactory vesselDetailsPopupFactory;
    private final VesselMap map;
    private Subscription vesselEventsSubscription;

    public MapRoute(VesselService vesselService, VesselDetailsPopupFactory vesselDetailsPopupFactory) {
        this.vesselService = vesselService;
        this.vesselDetailsPopupFactory = vesselDetailsPopupFactory;
        map = new VesselMap();
        map.setSizeFull();
        add(map);
        setSizeFull();
        map.setVesselClickedCallback(this::onVesselClicked);
        map.setEnvelopeChangedCallback(this::onEnvelopeChanged);
    }

    private void onVesselEvents(List<VesselEvent> vesselEvents) {
        getUI().ifPresent(ui -> ui.access(() -> vesselEvents.forEach(event -> {
            if (event instanceof VesselLocationOutdatedEvent e) {
                map.removeVessel(e.mmsi());
            } else if (event instanceof VesselLocationUpdatedEvent e) {
                map.addOrUpdateVessel(e.vesselLocation());
            }
        })));
    }

    private void onVesselClicked(MMSI mmsi) {
        vesselService.findVesselDetails(mmsi).map(vesselDetailsPopupFactory::create).ifPresent(Dialog::open);
    }

    private void onEnvelopeChanged(Envelope envelope) {
        map.addOrUpdateVessels(vesselService.vesselLocations(envelope, MAX_VESSELS_ON_MAP));
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        vesselEventsSubscription = vesselService.subscribeToVesselEvents(this::onVesselEvents);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        vesselEventsSubscription.unsubscribe();
    }
}
