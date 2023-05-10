package com.example.demo.ais.service.spi;

import com.example.demo.ais.service.dpo.VesselLocation;
import com.example.demo.ais.util.Result;
import com.example.demo.ais.util.Subscription;

import java.util.Collection;
import java.util.function.Consumer;


public interface AIS {

    Result<Collection<VesselLocation>> findAllVesselLocations();

    Subscription subscribeToVesselLocationChanges(Consumer<VesselLocation> subscriber);
}
