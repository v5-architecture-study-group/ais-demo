package com.example.demo.ais.service.spi;

import com.example.demo.ais.domain.data.VesselData;
import com.example.demo.ais.domain.data.VesselLocation;
import com.example.demo.ais.domain.events.VesselEvent;
import com.example.demo.ais.util.Result;
import com.example.demo.ais.util.Subscription;

import java.util.Collection;
import java.util.function.Consumer;


public interface AIS {

    Result<Collection<VesselLocation>> loadAllVesselLocations();

    Result<Collection<VesselData>> loadAllVesselData();

    Subscription subscribeToVesselEvents(Consumer<VesselEvent> subscriber);
}
