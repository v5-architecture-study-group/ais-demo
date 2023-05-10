package com.example.demo.ais.adapter.digitraffic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
record Geometry(String type, double[] coordinates) {
}
