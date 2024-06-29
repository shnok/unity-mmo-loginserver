package com.shnok.javaserver.dto.internal.gameserverpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class PlayerAuthRequestPacket extends ReceivablePacket {
    private final String account;
    private final int playOkID1;
    private final int playOkID2;
    private final int loginOkID1;
    private final int loginOkID2;
    public PlayerAuthRequestPacket(byte[] data) {
        super(data);

        account = readS();
        playOkID1 = readI();
        playOkID2 = readI();
        loginOkID1 = readI();
        loginOkID2 = readI();
    }
}