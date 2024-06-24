package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.external.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;
import com.shnok.javaserver.model.SessionKey;

public class LoginOkPacket extends ServerPacket {
    public LoginOkPacket(SessionKey sessionKey) {
        super(ServerPacketType.LoginOk.getValue());
        writeI(sessionKey.loginOkID1);
        writeI(sessionKey.loginOkID2);

        buildPacket();
    }
}