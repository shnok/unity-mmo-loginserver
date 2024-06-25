package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.AccountKickedReason;
import com.shnok.javaserver.enums.packettypes.ServerPacketType;

public class AccountKickedPacket extends SendablePacket {
    public AccountKickedPacket(AccountKickedReason kickedReason) {
        super(ServerPacketType.AccountKicked.getValue());
        writeB(kickedReason.getCode());
        buildPacket();
    }
}