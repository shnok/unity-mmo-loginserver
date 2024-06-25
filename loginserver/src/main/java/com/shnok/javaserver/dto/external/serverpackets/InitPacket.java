package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.ServerPacketType;

public class InitPacket extends SendablePacket {
    public InitPacket(byte[] publicKey, byte[] blowfishKey, int sessionId) {
        super(ServerPacketType.Init.getValue());
        writeI(sessionId);
        writeB(publicKey);
        writeB(blowfishKey);

        buildPacket();
    }
}