package com.example.demo.ais.adapter.digitraffic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
record Feature(int mmsi, String type, Geometry geometry, FeatureProperties properties) {
}
