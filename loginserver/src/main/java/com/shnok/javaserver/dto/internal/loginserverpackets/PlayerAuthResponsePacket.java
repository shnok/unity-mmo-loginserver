package com.shnok.javaserver.dto.internal.loginserverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.internal.LoginServerPacketType;

public class PlayerAuthResponsePacket extends SendablePacket {
    public PlayerAuthResponsePacket(String account, boolean authed) {
        super(LoginServerPacketType.PlayerAuthResponse.getValue());

        writeS(account);
        writeB(authed ? (byte) 1 : (byte) 0);

        buildPacket();
    }
}
