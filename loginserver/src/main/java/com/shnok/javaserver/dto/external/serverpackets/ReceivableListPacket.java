package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class ReceivableListPacket extends ReceivablePacket {
    public ReceivableListPacket() {
        super(ServerPacketType.ServerList.getValue());
        writeB((byte)(0));
        // TODO: return gameserver list
        buildPacket();
    }
}