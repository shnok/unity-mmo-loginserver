package com.shnok.javaserver.enums;

public enum LoginServerFailReason {
    REASON_INVALID_GAME_SERVER_VERSION(0),

    REASON_IP_BANNED(1),

    REASON_IP_RESERVED(2),

    REASON_WRONG_HEXID(3),

    REASON_ID_RESERVED(4),

    REASON_NO_FREE_ID(5),

    NOT_AUTHED(6),

    REASON_ALREADY_LOGGED_IN(7);
    private final int _code;

    LoginServerFailReason(int code) {
        _code = code;
    }

    public final int getCode() {
        return _code;
    }
}

