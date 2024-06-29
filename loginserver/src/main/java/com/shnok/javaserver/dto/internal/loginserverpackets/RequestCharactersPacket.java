package com.shnok.javaserver.dto.internal.loginserverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.LoginServerPacketType;

public class RequestCharactersPacket extends SendablePacket {
    public RequestCharactersPacket(String account) {
        super(LoginServerPacketType.RequestCharacters.getValue());

        writeS(account);

        buildPacket();
    }
}
