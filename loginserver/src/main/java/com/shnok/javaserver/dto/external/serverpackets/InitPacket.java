package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.external.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class InitPacket extends ServerPacket {
    public InitPacket(byte[] publicKey, byte[] blowfishKey, int sessionId) {
        super(ServerPacketType.Init.getValue());
        writeI(sessionId);
        writeB(publicKey);
        writeB(blowfishKey);

        buildPacket();
    }
}