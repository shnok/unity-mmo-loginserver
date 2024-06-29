package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.PlayFailReason;
import com.shnok.javaserver.enums.packettypes.external.ServerPacketType;

public class PlayFailPacket extends SendablePacket {
    public PlayFailPacket(PlayFailReason loginFailReason) {
        super(ServerPacketType.PlayFail.getValue());

        writeB((byte) loginFailReason.getCode());

        buildPacket();
    }
}
