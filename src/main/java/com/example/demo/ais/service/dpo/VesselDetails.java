package com.example.demo.ais.service.dpo;

import com.example.demo.ais.domain.base.Identifiable;
import com.example.demo.ais.domain.data.VesselData;
import com.example.demo.ais.domain.data.VesselLocation;
import com.example.demo.ais.domain.primitives.MMSI;
import jakarta.annotation.Nullable;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public final class VesselDetails implements Identifiable<MMSI> {
    private final MMSI mmsi;
    private final VesselData vesselData;
    private final VesselLocation vesselLocation;

    public VesselDetails(MMSI mmsi, @Nullable VesselData vesselData, @Nullable VesselLocation vesselLocation) {
        this.mmsi = requireNonNull(mmsi, "mmsi must not be null");
        if (vesselData != null && !mmsi.equals(vesselData.mmsi())) {
            throw new IllegalArgumentException("VesselData has wrong MMSI");
        }
        if (vesselLocation != null && !mmsi.equals(vesselLocation.mmsi())) {
            throw new IllegalArgumentException("VesselLocation has wrong MMSI");
        }
        this.vesselData = vesselData;
        this.vesselLocation = vesselLocation;
    }

    public MMSI mmsi() {
        return mmsi;
    }

    public Optional<VesselData> vesselData() {
        return Optional.ofNullable(vesselData);
    }

    public Optional<VesselLocation> vesselLocation() {
        return Optional.ofNullable(vesselLocation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VesselDetails that = (VesselDetails) o;
        return Objects.equals(mmsi, that.mmsi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mmsi);
    }

    @Override
    public MMSI id() {
        return mmsi;
    }
}
