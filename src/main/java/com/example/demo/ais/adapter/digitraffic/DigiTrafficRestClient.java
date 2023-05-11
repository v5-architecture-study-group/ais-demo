package com.example.demo.ais.adapter.digitraffic;

import com.example.demo.ais.domain.data.VesselData;
import com.example.demo.ais.domain.data.VesselLocation;
import com.example.demo.ais.domain.primitives.*;
import com.example.demo.ais.util.Result;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

class DigiTrafficRestClient {

    private static final Logger log = LoggerFactory.getLogger(DigiTrafficRestClient.class);
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(30);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    DigiTrafficRestClient() {
        restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setReadTimeout(READ_TIMEOUT)
                .build();
    }

    Result<Collection<VesselLocation>> loadAllVesselLocations() {
        return doLoadAllVesselLocations()
                .map(this::convertToVesselLocations)
                .doOnError(reason -> log.error("Error fetching vessel locations: {}", reason));
    }

    private Result<FeatureCollection> doLoadAllVesselLocations() {
        return doRestGet(Constants.VESSEL_LOCATIONS_URL, is -> objectMapper.readerFor(FeatureCollection.class).readValue(is));
    }

    private Collection<VesselLocation> convertToVesselLocations(FeatureCollection featureCollection) {
        return Arrays.stream(featureCollection.features())
                .flatMap(this::convertToVesselLocation)
                .toList();
    }

    private Stream<VesselLocation> convertToVesselLocation(Feature feature) {
        try {
            var timestamp = Instant.ofEpochMilli(feature.properties().timestampExternal());
            var mmsi = MMSI.fromInt(feature.mmsi());
            if (!"point".equalsIgnoreCase(feature.geometry().type())) {
                log.warn("Unknown geometry while parsing vessel location");
                return Stream.empty();
            }
            var lon = new Longitude(feature.geometry().coordinates()[0]);
            var lat = new Latitude(feature.geometry().coordinates()[1]);
            var heading = Heading.ofDegrees(feature.properties().heading());
            var position = feature.properties().posAcc() ? new AccuratePosition(lat, lon) : new InaccuratePosition(lat, lon);
            return Stream.of(new VesselLocation(timestamp, mmsi, position, heading));
        } catch (Throwable ex) {
            log.debug(feature.toString());
            log.debug("Exception while parsing vessel location", ex);
            return Stream.empty();
        }
    }

    Result<Collection<VesselData>> loadAllVesselData() {
        return doLoadAllVesselData()
                .map(this::convertToVesselData)
                .doOnError(reason -> log.error("Error fetching vessel data: {}", reason));
    }

    private Result<List<VesselMetadata>> doLoadAllVesselData() {
        return doRestGet(Constants.VESSEL_DATA_URL, is -> objectMapper.readerForListOf(VesselMetadata.class).readValue(is));
    }

    private Collection<VesselData> convertToVesselData(Collection<VesselMetadata> vesselMetadata) {
        return vesselMetadata.stream()
                .flatMap(this::convertToVesselData)
                .toList();
    }

    private Stream<VesselData> convertToVesselData(VesselMetadata vesselMetadata) {
        try {
            var timestamp = Instant.ofEpochMilli(vesselMetadata.timestamp());
            var mmsi = MMSI.fromInt(vesselMetadata.mmsi);
            var vesselName = new VesselName(vesselMetadata.name());
            var callSign = new CallSign(vesselMetadata.callSign());
            var shipType = new ShipType(vesselMetadata.shipType());
            return Stream.of(new VesselData(timestamp, mmsi, vesselName, callSign, shipType));
        } catch (Throwable ex) {
            log.debug("Exception while parsing vessel metadata", ex);
            return Stream.empty();
        }
    }

    @FunctionalInterface
    private interface ResultParser<R> {
        R parseInputStream(InputStream inputStream) throws IOException;
    }

    @SuppressWarnings("DataFlowIssue")
    private <R> Result<R> doRestGet(String url, ResultParser<R> resultMapper) {
        final var httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.ACCEPT_ENCODING, "gzip");
        httpHeaders.set("Digitraffic-User", Constants.APPLICATION_NAME);

        try {
            var response = restTemplate.exchange(
                    url,
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
                return Result.success(resultMapper.parseInputStream(gzipInputStream));
            }
        } catch (Throwable ex) {
            return Result.failure(ex);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record FeatureCollection(String type, String dataUpdatedTime, Feature[] features) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Feature(int mmsi, String type, Geometry geometry, FeatureProperties properties) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Geometry(String type, double[] coordinates) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record FeatureProperties(
            int mmsi,
            double sog,
            double cog,
            int navStat,
            int rot,
            boolean posAcc,
            boolean raim,
            int heading,
            long timestamp,
            long timestampExternal
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record VesselMetadata(
            long timestamp,
            String destination,
            int mmsi,
            String callSign,
            int imo,
            int shipType,
            int draught,
            long eta,
            int posType,
            int referencePointA,
            int referencePointB,
            int referencePointC,
            int referencePointD,
            String name
    ) {
    }
}
