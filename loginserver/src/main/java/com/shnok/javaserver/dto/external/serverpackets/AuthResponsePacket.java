package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class AuthResponsePacket extends ReceivablePacket {

    public AuthResponsePacket(AuthResponseType reason) {
        super(ServerPacketType.AuthResponse.getValue());
        writeB((byte) reason.ordinal());
        buildPacket();
    }

    public enum AuthResponseType {
        ALLOW,
        ALREADY_CONNECTED,
        INVALID_USERNAME
    }
}
