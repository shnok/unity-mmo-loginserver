package com.shnok.javaserver.dto.internal.loginserverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.internal.LoginServerPacketType;

public class ReceivableListPacket extends SendablePacket {
    public ReceivableListPacket() {
        super(LoginServerPacketType.ReceivableList.getValue());
        buildPacket();
    }
}