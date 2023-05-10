package com.example.demo.ais.adapter.digitraffic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
record FeatureCollection(String type, String dataUpdatedTime, Feature[] features) {
}
