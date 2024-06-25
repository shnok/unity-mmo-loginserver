package com.shnok.javaserver.dto.internal.loginserverpackets;

import com.shnok.javaserver.dto.SendablePacket;

public class LoginServerFailPacket extends SendablePacket {
    public LoginServerFailPacket(int failReason) {
        super(null);
    }
}
