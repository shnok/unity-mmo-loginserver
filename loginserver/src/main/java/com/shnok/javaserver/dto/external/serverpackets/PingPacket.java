package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.external.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class PingPacket extends ServerPacket {
    public PingPacket() {
        super(ServerPacketType.Ping.getValue());
        setData(new byte[]{ServerPacketType.Ping.getValue(), 0x02});
    }
}

