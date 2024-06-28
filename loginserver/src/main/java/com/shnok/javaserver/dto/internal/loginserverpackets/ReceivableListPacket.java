package com.shnok.javaserver.dto.internal.loginserverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.LoginServerPacketType;
import com.shnok.javaserver.enums.packettypes.ServerPacketType;

public class ReceivableListPacket extends SendablePacket {
    public ReceivableListPacket() {
        super(LoginServerPacketType.ReceivableList.getValue());
        writeB((byte)(0));
        // TODO: send gameserver list
        buildPacket();
    }
}