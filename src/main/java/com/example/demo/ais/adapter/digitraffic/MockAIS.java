package com.example.demo.ais.adapter.digitraffic;

import com.example.demo.ais.domain.data.VesselData;
import com.example.demo.ais.domain.data.VesselLocation;
import com.example.demo.ais.domain.events.VesselEvent;
import com.example.demo.ais.service.spi.AIS;
import com.example.demo.ais.util.Result;
import com.example.demo.ais.util.Subscription;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

class MockAIS implements AIS {

    @Override
    public Result<Collection<VesselLocation>> loadAllVesselLocations() {
        return Result.success(Collections.emptyList());
    }

    @Override
    public Result<Collection<VesselData>> loadAllVesselData() {
        return Result.success(Collections.emptyList());
    }

    @Override
    public Subscription subscribeToVesselEvents(Consumer<VesselEvent> subscriber) {
        return () -> {
            // NOP
        };
    }
}
