package com.example.demo.ais.service.spi;

import com.example.demo.ais.domain.primitives.MMSI;
import com.example.demo.ais.service.dpo.VesselLocation;
import com.example.demo.ais.util.Result;
import com.example.demo.ais.util.Subscription;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;


public interface AIS {

    Map<MMSI, Result<VesselLocation>> findVesselLocations(Collection<MMSI> vesselIdentifiers);

    Result<Collection<VesselLocation>> findAllVesselLocations();

    Subscription subscribeToVesselLocationChanges(Consumer<Collection<VesselLocation>> subscriber);

}
