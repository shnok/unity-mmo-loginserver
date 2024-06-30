package com.shnok.javaserver.dto.internal.gameserverpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class ReplyCharactersPacket extends ReceivablePacket {
    private final String account;
    private final int charCount;

    public ReplyCharactersPacket(byte[] data) {
        super(data);

        account = readS();
        charCount = readB();
    }
}
