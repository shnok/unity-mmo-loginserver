package com.shnok.javaserver.dto.internal.gameserverpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class GameServerAuthPacket extends ReceivablePacket {
    private final byte id;
    private final boolean acceptAlternate;
    private final int port;
    private final int maxPlayer;
    private final int hexIdLength;
    private final byte[] hexId;
    private final int subnetSize;
    private final String[] hosts;

    public GameServerAuthPacket(byte[] data) {
        super(data);

        id = readB();
        acceptAlternate = readB() == 1;
        port = readI();
        maxPlayer = readI();
        hexIdLength = readI();
        hexId = readB(hexIdLength);
        subnetSize = readI();
        hosts = new String[subnetSize * 2];
        for(int i = 0; i < hosts.length; i++) {
            hosts[i] = readS();
        }
    }
}
