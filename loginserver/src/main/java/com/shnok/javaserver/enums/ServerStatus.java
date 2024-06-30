package com.shnok.javaserver.enums;

public enum ServerStatus {
    STATUS_AUTO((byte) 0x00),
    STATUS_GOOD((byte) 0x01),
    STATUS_NORMAL((byte) 0x02),
    STATUS_FULL((byte) 0x03),
    STATUS_DOWN((byte) 0x04),
    STATUS_GM_ONLY((byte) 0x05);

    private final byte _code;

    ServerStatus(byte code) {
        _code = code;
    }

    public final byte getCode() {
        return _code;
    }
}
