package com.shnok.javaserver.enums;

import java.util.HashMap;
import java.util.Map;

public enum ServerPacketType {
    Ping((byte)0),
    Init((byte)1),
    AuthResponse((byte)2),
    LoginFailResponse((byte)3),
    AccountKicked((byte) 4),
    ServerList((byte) 5),
    LoginOk((byte) 6);

    private final byte value;

    ServerPacketType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    private static final Map<Byte, ServerPacketType> BY_VALUE = new HashMap<>();

    static {
        for (ServerPacketType type : values()) {
            BY_VALUE.put(type.getValue(), type);
        }
    }

    public static ServerPacketType fromByte(byte value) {
        ServerPacketType result = BY_VALUE.get(value);
        if (result == null) {
            throw new IllegalArgumentException("Invalid byte value for ClientPacketType: " + value);
        }
        return result;
    }
}
