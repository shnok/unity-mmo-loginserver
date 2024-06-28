package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class RequestServerListPacket extends ReceivablePacket {
    private int skey1;
    private int skey2;

    public RequestServerListPacket(byte[] data) {
        super(data);

        skey1 = readI();
        skey2 = readI();
    }
}
