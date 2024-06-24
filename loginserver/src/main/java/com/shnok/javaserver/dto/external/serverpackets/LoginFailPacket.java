package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.external.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;
import com.shnok.javaserver.enums.LoginFailReason;

public class LoginFailPacket extends ServerPacket {
    public LoginFailPacket(LoginFailReason loginFailReason) {
        super(ServerPacketType.AuthResponse.getValue());
        writeB((byte) loginFailReason.getCode());
        buildPacket();
    }
}
