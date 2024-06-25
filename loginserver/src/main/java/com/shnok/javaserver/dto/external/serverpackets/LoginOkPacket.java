package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import com.shnok.javaserver.enums.ServerPacketType;
import com.shnok.javaserver.model.SessionKey;

public class LoginOkPacket extends ReceivablePacket {
    public LoginOkPacket(SessionKey sessionKey) {
        super(ServerPacketType.LoginOk.getValue());
        writeI(sessionKey.loginOkID1);
        writeI(sessionKey.loginOkID2);

        buildPacket();
    }
}