package com.shnok.javaserver.dto.internal.loginserverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.internal.LoginServerPacketType;

public class KickPlayerPacket extends SendablePacket {
    public KickPlayerPacket(String account) {
        super(LoginServerPacketType.KickPlayer.getValue());

        writeS(account.toLowerCase().trim());

        buildPacket();
    }
}
