package com.example.demo.ais.adapter.ui;

import com.example.demo.ais.domain.data.VesselLocation;
import com.example.demo.ais.domain.primitives.*;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.map.events.MapFeatureClickEvent;
import com.vaadin.flow.component.map.events.MapViewMoveEndEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

class VesselMap extends Composite<Map> implements HasSize {

    private static final Logger log = LoggerFactory.getLogger(VesselMap.class);
    private final HashMap<MMSI, VesselMarker> markers = new HashMap<>();
    private Envelope envelope;
    private Consumer<Envelope> envelopeChangedCallback;
    private Consumer<MMSI> vesselClickedCallback;

    VesselMap() {
        updateEnvelope();
        getContent().addFeatureClickListener(this::onMapFeatureClickEvent);
        getContent().addViewMoveEndEventListener(this::onMapViewMoveEndEvent);
    }

    public void setEnvelopeChangedCallback(Consumer<Envelope> callback) {
        envelopeChangedCallback = callback;
        if (callback != null) {
            callback.accept(envelope);
        }
    }

    public void setVesselClickedCallback(Consumer<MMSI> callback) {
        vesselClickedCallback = callback;
    }

    public void removeVessel(MMSI vesselIdentifier) {
        var marker = markers.remove(vesselIdentifier);
        if (marker != null) {
            marker.removeFromMap();
        }
    }

    private void removeInvisibleVessels() {
        var vesselsToRemove = markers.values().stream().filter(VesselMarker::isInvisible).map(VesselMarker::mmsi).toList();
        log.trace("Removing {} invisible vessels", vesselsToRemove.size());
        vesselsToRemove.forEach(this::removeVessel);
    }

    private boolean isVisible(Position position) {
        return envelope.contains(position);
    }

    public void addOrUpdateVessel(VesselLocation vessel) {
        if (isVisible(vessel.position())) {
            getMarker(vessel.mmsi()).update(vessel);
        } else {
            removeVessel(vessel.mmsi());
        }
    }

    public void addOrUpdateVessels(Collection<VesselLocation> vessels) {
        log.trace("Adding or updating {} vessels", vessels.size());
        vessels.forEach(this::addOrUpdateVessel);
    }

    private VesselMarker getMarker(MMSI mmsi) {
        return markers.computeIfAbsent(mmsi, VesselMarker::new);
    }

    public State getState() {
        return new State(getContent().getCenter(), getContent().getZoom());
    }

    public void setState(State state) {
        Objects.requireNonNull(state, "state must not be null");
        getContent().setCenter(state.center());
        getContent().setZoom(state.zoom());
    }

    record State(
            Coordinate center,
            double zoom
    ) {
    }

    private void onMapFeatureClickEvent(MapFeatureClickEvent event) {
        if (vesselClickedCallback != null) {
            var mmsi = new MMSI(event.getFeature().getId());
            log.trace("Clicked on vessel {}", mmsi.value());
            vesselClickedCallback.accept(mmsi);
        }
    }

    private void onMapViewMoveEndEvent(MapViewMoveEndEvent event) {
        updateEnvelope();
    }

    private void updateEnvelope() {
        var extent = getContent().getView().getExtent();

        var old = envelope;
        envelope = new Envelope(
                new Latitude(extent.getMinY()),
                new Longitude(extent.getMinX()),
                new Latitude(extent.getMaxY()),
                new Longitude(extent.getMaxX())
        );

        if (!Objects.equals(old, envelope) && envelopeChangedCallback != null) {
            envelopeChangedCallback.accept(envelope);
            removeInvisibleVessels();
        }
    }

    private class VesselMarker {
        private final MarkerFeature marker = new MarkerFeature();
        private final MMSI mmsi;
        private Position position;

        VesselMarker(MMSI mmsi) {
            this.mmsi = mmsi;
            marker.setId(mmsi.value());
            getContent().getFeatureLayer().addFeature(marker);
        }

        void update(VesselLocation vesselLocation) {
            position = vesselLocation.position();
            if (vesselLocation.sog().knotTenths() == 0) {
                marker.setIcon(VesselMarkerIcons.getNonMovingVesselIcon(vesselLocation.heading()));
            } else {
                marker.setIcon(VesselMarkerIcons.getMovingVesselIcon(vesselLocation.heading()));
            }
            marker.setCoordinates(new Coordinate(vesselLocation.position().longitude().value(),
                    vesselLocation.position().latitude().value()));
        }

        MMSI mmsi() {
            return mmsi;
        }

        boolean isInvisible() {
            return position == null || !isVisible(position);
        }

        void removeFromMap() {
            getContent().getFeatureLayer().removeFeature(marker);
        }
    }
}
