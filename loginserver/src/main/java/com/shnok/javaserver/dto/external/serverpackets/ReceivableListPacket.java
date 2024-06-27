package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.ServerPacketType;

public class ReceivableListPacket extends SendablePacket {
    public ReceivableListPacket() {
        super(ServerPacketType.ReceivableList.getValue());
        writeB((byte)(0));
        // TODO: send gameserver list
        buildPacket(true);
    }
}