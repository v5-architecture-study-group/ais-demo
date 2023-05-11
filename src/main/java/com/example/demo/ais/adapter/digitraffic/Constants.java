package com.example.demo.ais.adapter.digitraffic;

final class Constants {

    private Constants() {
    }

    static final String APPLICATION_NAME = "Vaadin.com / PH / AIS Demo App";
    static final String VESSEL_LOCATIONS_URL = "https://meri.digitraffic.fi/api/ais/v1/locations";
    static final String VESSEL_DATA_URL = "https://meri.digitraffic.fi/api/ais/v1/vessels";
    static final String MQTT_URL = "wss://meri.digitraffic.fi:443/mqtt";
    static final String ALL_LOCATIONS_TOPIC = "vessels-v2/+/location";
    static final String ALL_VESSELS_TOPIC = "vessels-v2/+/metadata";

}
