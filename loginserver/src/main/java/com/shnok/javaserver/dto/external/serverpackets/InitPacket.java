package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.ServerPacketType;

public class InitPacket extends SendablePacket {
    public InitPacket(byte[] publicKey, byte[] blowfishKey, int sessionId) {
        super(ServerPacketType.Init.getValue());
        writeI(sessionId);
        writeI(publicKey.length);
        writeB(publicKey);
        writeI(blowfishKey.length);
        writeB(blowfishKey);

        buildPacket();
    }
}