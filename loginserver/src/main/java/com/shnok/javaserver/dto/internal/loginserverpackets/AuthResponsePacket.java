package com.shnok.javaserver.dto.internal.loginserverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.internal.LoginServerPacketType;

public class AuthResponsePacket extends SendablePacket {
    public AuthResponsePacket(int id) {
        super(LoginServerPacketType.AuthResponse.getValue());
        writeB((byte) 0);
        writeB((byte) 0);
        writeI(id);

        buildPacket();
    }
}