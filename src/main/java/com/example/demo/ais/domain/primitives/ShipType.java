package com.example.demo.ais.domain.primitives;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ShipType {

    private static final Map<Integer, String> SHIP_TYPE_CODES;

    static {
        var shipTypes = new HashMap<Integer, String>();
        shipTypes.put(0, "Not available");
        shipTypes.putAll(Map.of(
                20, "Wing in ground (WIG)",
                21, "Wing in ground (WIG), Hazardous category A",
                22, "Wing in ground (WIG), Hazardous category B",
                23, "Wing in ground (WIG), Hazardous category C",
                24, "Wing in ground (WIG), Hazardous category D"
        ));
        shipTypes.putAll(Map.of(
                30, "Fishing",
                31, "Towing",
                32, "Towing: length exceeds 200m or breadth exceeds 25m",
                33, "Dredging or underwater ops",
                34, "Diving ops",
                35, "Military ops",
                36, "Sailing",
                37, "Pleasure Craft"
        ));
        shipTypes.putAll(Map.of(
                40, "High speed craft (HSC)",
                41, "High speed craft (HSC), Hazardous category A",
                42, "High speed craft (HSC), Hazardous category B",
                43, "High speed craft (HSC), Hazardous category C",
                44, "High speed craft (HSC), Hazardous category D",
                49, "High speed craft (HSC), No additional information"
        ));
        shipTypes.putAll(Map.of(
                50, "Pilot Vessel",
                51, "Search and Rescue vessel",
                52, "Tug",
                53, "Port Tender",
                54, "Anti-pollution equipment",
                55, "Law Enforcement",
                56, "Spare - Local Vessel",
                57, "Spare - Local Vessel",
                58, "Medical Transport",
                59, "Noncombatant ship according to RR Resolution No. 18"
        ));
        shipTypes.putAll(Map.of(
                60, "Passenger",
                61, "Passenger, Hazardous category A",
                62, "Passenger, Hazardous category B",
                63, "Passenger, Hazardous category C",
                64, "Passenger, Hazardous category D",
                69, "Passenger, No additional information"
        ));
        shipTypes.putAll(Map.of(
                70, "Cargo",
                71, "Cargo, Hazardous category A",
                72, "Cargo, Hazardous category B",
                73, "Cargo, Hazardous category C",
                74, "Cargo, Hazardous category D",
                79, "Cargo, No additional information"
        ));
        shipTypes.putAll(Map.of(
                80, "Tanker",
                81, "Tanker, Hazardous category A",
                82, "Tanker, Hazardous category B",
                83, "Tanker, Hazardous category C",
                84, "Tanker, Hazardous category D",
                89, "Tanker, No additional information"
        ));
        shipTypes.putAll(Map.of(
                90, "Other Type",
                91, "Other Type, Hazardous category A",
                92, "Other Type, Hazardous category B",
                93, "Other Type, Hazardous category C",
                94, "Other Type, Hazardous category D",
                99, "Other Type, no additional information"
        ));
        SHIP_TYPE_CODES = Collections.unmodifiableMap(shipTypes);
    }

    private final int shipType;

    public ShipType(int shipType) {
        if (shipType < 0 || shipType > 999) {
            throw new IllegalArgumentException("shipType must be between 0 and 999");
        }
        this.shipType = shipType;
    }

    public int value() {
        return shipType;
    }

    public String description() {
        return SHIP_TYPE_CODES.getOrDefault(shipType, "Unknown (%d)".formatted(shipType));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShipType shipType1 = (ShipType) o;
        return shipType == shipType1.shipType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shipType);
    }
}
