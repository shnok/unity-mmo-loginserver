package com.shnok.javaserver.dto.internal.loginserverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.LoginServerPacketType;

public class InitLSPacket extends SendablePacket {
    public InitLSPacket(byte[] publicKey) {
        super(LoginServerPacketType.InitLS.getValue());
        writeB(publicKey);
        buildPacket();
    }
}
