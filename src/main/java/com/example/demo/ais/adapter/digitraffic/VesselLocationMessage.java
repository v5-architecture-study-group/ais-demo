package com.example.demo.ais.adapter.digitraffic;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
record VesselLocationMessage(
        long time,
        double sog,
        double cog,
        int navStat,
        int rot,
        boolean posAcc,
        boolean raim,
        int heading,
        double lon,
        double lat) {
}
