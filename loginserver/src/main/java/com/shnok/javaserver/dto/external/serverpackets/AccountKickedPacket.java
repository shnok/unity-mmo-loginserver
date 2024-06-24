package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.external.ServerPacket;
import com.shnok.javaserver.enums.AccountKickedReason;
import com.shnok.javaserver.enums.ServerPacketType;

public class AccountKickedPacket extends ServerPacket {
    public AccountKickedPacket(AccountKickedReason kickedReason) {
        super(ServerPacketType.AccountKicked.getValue());
        writeB(kickedReason.getCode());
        buildPacket();
    }
}