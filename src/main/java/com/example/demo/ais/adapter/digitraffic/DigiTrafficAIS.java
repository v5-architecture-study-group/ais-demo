package com.example.demo.ais.adapter.digitraffic;

import com.example.demo.ais.domain.primitives.*;
import com.example.demo.ais.service.dpo.VesselLocation;
import com.example.demo.ais.service.spi.AIS;
import com.example.demo.ais.util.Result;
import com.example.demo.ais.util.Subscription;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@Component
class DigiTrafficAIS implements AIS {

    private static final String VESSEL_LOCATIONS_URL = "https://meri.digitraffic.fi/api/ais/v1/locations";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public DigiTrafficAIS() {
        restTemplate = new RestTemplate();
        objectMapper = new ObjectMapper();
    }

    @Override
    public Map<MMSI, Result<VesselLocation>> findVesselLocations(Collection<MMSI> vesselIdentifiers) {
        return null;
    }

    @Override
    public Result<Collection<VesselLocation>> findAllVesselLocations() {
        return doFindVesselLocations().map(this::featureCollectionToVesselLocations);
    }

    private Result<FeatureCollection> doFindVesselLocations() {
        final var httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.ACCEPT_ENCODING, "gzip");

        try {
            var response = restTemplate.exchange(
                    VESSEL_LOCATIONS_URL,
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    Resource.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                return Result.failure("Server responded with error %d".formatted(response.getStatusCode().value()));
            }

            if (!response.hasBody()) {
                return Result.failure("Response had no body");
            }

            var contentEncoding = response.getHeaders().getFirst(HttpHeaders.CONTENT_ENCODING);
            if (!"gzip".equalsIgnoreCase(contentEncoding)) {
                return Result.failure("Unknown content encoding");
            }

            try (GZIPInputStream gzipInputStream = new GZIPInputStream(response.getBody().getInputStream())) {
                return Result.success(objectMapper.readerFor(FeatureCollection.class).readValue(gzipInputStream));
            }
        } catch (IOException | RestClientException ex) {
            return Result.failure(ex.getMessage());
        }
    }

    private Collection<VesselLocation> featureCollectionToVesselLocations(FeatureCollection featureCollection) {
        return Arrays.stream(featureCollection.features())
                .map(this::featureToVesselLocation)
                .filter(Result::isSuccessful)
                .map(Result::get)
                .collect(Collectors.toList());
    }

    private Result<VesselLocation> featureToVesselLocation(Feature feature) {
        try {
            var timestamp = Instant.ofEpochMilli(feature.properties().timestampExternal());
            var mmsi = new MMSI(String.valueOf(feature.mmsi()));
            if (!"point".equalsIgnoreCase(feature.geometry().type())) {
                return Result.failure("Unknown geometry");
            }
            var lon = new Longitude(feature.geometry().coordinates()[0]);
            var lat = new Latitude(feature.geometry().coordinates()[1]);
            var heading = new Heading(feature.properties().heading());
            var position = feature.properties().posAcc() ? new AccuratePosition(lat, lon) : new InaccuratePosition(lat, lon);
            return Result.success(new VesselLocation(timestamp, mmsi, position, heading));
        } catch (Exception ex) {
            return Result.failure(ex.getMessage());
        }
    }

    @Override
    public Subscription subscribeToVesselLocationChanges(Consumer<Collection<VesselLocation>> subscriber) {
        return null;
    }


    public static void main(String[] args) {
        var result = new DigiTrafficAIS().findAllVesselLocations();
        if (result.isSuccessful()) {
            System.out.println(result.get());
        } else {
            System.out.println(result.reason());
        }
    }
}
