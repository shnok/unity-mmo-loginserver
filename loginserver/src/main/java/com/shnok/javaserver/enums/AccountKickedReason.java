package com.shnok.javaserver.enums;

public enum AccountKickedReason {
    REASON_DATA_STEALER((byte) 0x01),
    REASON_GENERIC_VIOLATION((byte) 0x08),
    REASON_7_DAYS_SUSPENDED((byte) 0x10),
    REASON_PERMANENTLY_BANNED((byte) 0x20);

    private final byte _code;

    AccountKickedReason(byte code) {
        _code = code;
    }

    public final byte getCode() {
        return _code;
    }
}
