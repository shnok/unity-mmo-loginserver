package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.external.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class ServerListPacket extends ServerPacket {
    public ServerListPacket() {
        super(ServerPacketType.ServerList.getValue());
        writeB((byte)(0));
        // TODO: return gameserver list
        buildPacket();
    }
}