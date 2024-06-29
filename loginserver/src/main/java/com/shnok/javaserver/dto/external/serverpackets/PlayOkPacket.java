package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.SessionKey;

public class PlayOkPacket extends SendablePacket {
    public PlayOkPacket(SessionKey sessionKey) {
        super(ServerPacketType.PlayOk.getValue());

        writeI(sessionKey.playOkID1);
        writeI(sessionKey.playOkID2);

        buildPacket();
    }
}
