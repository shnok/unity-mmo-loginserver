package com.shnok.javaserver.dto.internal.gameserverpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class PlayerLogoutPacket extends ReceivablePacket {
    private final String player;

    public PlayerLogoutPacket(byte[] data) {
        super(data);

       player = readS();
    }
}
