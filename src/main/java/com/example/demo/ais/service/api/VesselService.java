package com.example.demo.ais.service.api;

import com.example.demo.ais.domain.data.VesselData;
import com.example.demo.ais.domain.data.VesselLocation;
import com.example.demo.ais.domain.events.VesselEvent;
import com.example.demo.ais.domain.primitives.Envelope;
import com.example.demo.ais.domain.primitives.MMSI;
import com.example.demo.ais.util.Subscription;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface VesselService {

    Collection<VesselLocation> vesselLocations(Envelope envelope);

    Optional<VesselData> findVesselData(MMSI mmsi);

    Collection<VesselData> findVesselData(String searchTerm);

    Subscription subscribeToVesselEvents(Consumer<List<VesselEvent>> listener);
}
