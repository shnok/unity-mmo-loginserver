package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class RequestServerLoginPacket extends ReceivablePacket {
    private final int skey1;
    private final int skey2;
    private final int serverId;

    public RequestServerLoginPacket(byte[] data) {
        super(data);

        skey1 = readI();
        skey2 = readI();
        serverId = readI();
    }
}
