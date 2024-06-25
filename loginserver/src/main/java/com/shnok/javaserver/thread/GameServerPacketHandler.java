package com.shnok.javaserver.thread;

import com.shnok.javaserver.dto.external.serverpackets.PingPacket;
import com.shnok.javaserver.enums.ClientPacketType;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.interfaces.RSAPrivateKey;

import static com.shnok.javaserver.config.Configuration.server;

@Log4j2
public class GameServerPacketHandler extends Thread {
    private final GameServerThread gameserver;
    private final byte[] data;

    public GameServerPacketHandler(GameServerThread gameserver, byte[] data) {
        this.gameserver = gameserver;
        this.data = data;
    }

    @Override
    public void run() {
        handle();
    }

    public void handle() {
        ClientPacketType type = ClientPacketType.fromByte(data[0]);

        if(type != ClientPacketType.Ping) {
            log.debug("Received packet: {}", type);
        }

        switch (type) {
            case Ping:
                onReceiveEcho();
                break;
            case AuthRequest:
                onReceiveAuth(data, gameserver.getPrivateKey());
                break;
        }
    }

    private void onReceiveEcho() {
        gameserver.sendPacket(new PingPacket());

        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (System.currentTimeMillis() - gameserver.getLastEcho() >= server.serverConnectionTimeoutMs()) {
                    log.info("User connection timeout.");
                    gameserver.disconnect();
                }
            }
        });
        timer.setRepeats(false);
        timer.start();

        gameserver.setLastEcho(System.currentTimeMillis(), timer);
    }

    private void onReceiveAuth(byte[] data, RSAPrivateKey privateKey) {
    }
}