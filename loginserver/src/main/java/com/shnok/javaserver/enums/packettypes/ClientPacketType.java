package com.shnok.javaserver.enums.packettypes;

import java.util.HashMap;
import java.util.Map;

public enum ClientPacketType {
    Ping((byte)0),
    AuthRequest((byte)1),
    RequestServerList((byte)2);

    private final byte value;

    ClientPacketType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    private static final Map<Byte, ClientPacketType> BY_VALUE = new HashMap<>();

    static {
        for (ClientPacketType type : values()) {
            BY_VALUE.put(type.getValue(), type);
        }
    }

    public static ClientPacketType fromByte(byte value) {
        ClientPacketType result = BY_VALUE.get(value);
        if (result == null) {
            throw new IllegalArgumentException("Invalid byte value for ClientPacketType: " + value);
        }
        return result;
    }
}
