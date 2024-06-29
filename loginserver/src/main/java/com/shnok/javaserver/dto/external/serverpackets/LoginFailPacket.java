package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.external.ServerPacketType;
import com.shnok.javaserver.enums.LoginFailReason;

public class LoginFailPacket extends SendablePacket {
    public LoginFailPacket(LoginFailReason loginFailReason) {
        super(ServerPacketType.LoginFail.getValue());
        writeB((byte) loginFailReason.getCode());

        buildPacket();
    }
}
