package com.shnok.javaserver.dto.internal.loginserverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.LoginServerPacketType;

public class LoginServerFailPacket extends SendablePacket {
    public LoginServerFailPacket(int failReason) {
        super(LoginServerPacketType.Fail.getValue());
        writeB((byte) 0);
        writeB((byte) 0);
        writeI(failReason);
        buildPacket();
    }
}
