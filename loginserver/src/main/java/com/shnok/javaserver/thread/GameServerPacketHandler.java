package com.shnok.javaserver.thread;

import com.shnok.javaserver.dto.external.serverpackets.PingPacket;
import com.shnok.javaserver.dto.internal.gameserverpackets.BlowFishKeyPacket;
import com.shnok.javaserver.dto.internal.gameserverpackets.GameServerAuthPacket;
import com.shnok.javaserver.enums.LoginServerFailReason;
import com.shnok.javaserver.enums.packettypes.ClientPacketType;
import com.shnok.javaserver.enums.packettypes.GameServerPacketType;
import com.shnok.javaserver.enums.packettypes.LoginServerPacketType;
import com.shnok.javaserver.model.GameServerInfo;
import com.shnok.javaserver.security.NewCrypt;
import com.shnok.javaserver.service.GameServerController;
import com.shnok.javaserver.service.db.GameServerTable;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;

import static com.shnok.javaserver.config.Configuration.server;
import static com.shnok.javaserver.enums.GameServerState.BF_CONNECTED;

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
        log.debug("<--- Encrypted packet {} : {}", data.length, Arrays.toString(data));
        gameserver.getBlowfish().decrypt(data, 0, data.length);
        log.debug("<--- Decrypted packet {} : {}", data.length, Arrays.toString(data));

        GameServerPacketType type = GameServerPacketType.fromByte(data[0]);

        log.debug("Received packet: {}", type);

        switch (type) {
            case BlowFishKey:
                onReceiveBlowfishKey(data);
                break;
            case AuthRequest:
                onReceiveAuthRequest(data);
                break;
        }
    }

    private void onReceiveBlowfishKey(byte[] data) {
        BlowFishKeyPacket packet = new BlowFishKeyPacket(data, gameserver);

        gameserver.setBlowfish(new NewCrypt(packet.getBlowFishKey()));

        gameserver.setLoginConnectionState(BF_CONNECTED);
    }

    private void onReceiveAuthRequest(byte[] data) {
        GameServerAuthPacket packet = new GameServerAuthPacket(data);

        log.info("Auth request received.");

        if (handleRegProcess(packet)) {
//            server.sendPacket(new AuthResponse(server.getGameServerInfo().getId()));
//            LOG.info("Game Server {} enabled.", GameServerVersion.valueOf(_serverVersion));
//
//            server.broadcastToTelnet("GameServer [" + server.getServerId() + "] " + ServerNameDAO.getServer(server.getServerId()) + " is connected");
//            server.setLoginConnectionState(GameServerState.AUTHED);
        }
    }

    private boolean handleRegProcess(GameServerAuthPacket packet) {
        GameServerController gameServerController = GameServerController.getInstance();

        GameServerInfo gsi = gameServerController.getRegisteredGameServerById(packet.getId());

        if(true) {
            gameserver.forceClose(LoginServerFailReason.REASON_NO_FREE_ID.getCode());
            return false;
        }


        // is there a game server registered with this id?
        if (gsi != null) {
            log.debug("A gameserver is already registered with ID {}", packet.getId());
            // does the hex id match?
            if (Arrays.equals(gsi.getHexId(), packet.getHexId())) {
                log.debug("Gameserver hexId matches {}", Arrays.toString(packet.getHexId()));
                // check to see if this GS is already connected
                synchronized (gsi) {
                    if (gsi.isAuthed()) {
                        log.error("Gameserver already authed");
                        gameserver.forceClose(LoginServerFailReason.REASON_ALREADY_LOGGED_IN.getCode());
                        return false;
                    }
                    gameserver.attachGameServerInfo(gsi, packet.getPort(), packet.getHosts(), packet.getMaxPlayer());
                }
            } else {
                log.debug("There is already a server registered with the desired id and different hex id.");
                // there is already a server registered with the desired id and different hex id
                // try to register this one with an alternative id
                if (server.acceptNewGameserver() && packet.isAcceptAlternate()) {
                    log.debug("Trying to register this one with an alternative id.");
                    gsi = new GameServerInfo(packet.getId(), packet.getHexId(), gameserver);
                    if (gameServerController.registerWithFirstAvailableId(gsi)) {
                        gameserver.attachGameServerInfo(gsi, packet.getPort(), packet.getHosts(), packet.getMaxPlayer());
                        //TODO: Register in DB
                        //gameServerTable.registerServerOnDB(gsi);
                    } else {
                        log.debug("No free gameserver id remaining.");
                        gameserver.forceClose(LoginServerFailReason.REASON_NO_FREE_ID.getCode());
                        return false;
                    }
                } else {
                    // server id is already taken, and we cant get a new one for you
                    log.error("Server id is already taken, and we cant get a new one for you.");
                    gameserver.forceClose(LoginServerFailReason.REASON_WRONG_HEXID.getCode());
                    return false;
                }
            }
        } else {
            log.debug("A new unregistered gameserver received with ID: {}.", packet.getId());
            // can we register on this id?
            if (server.acceptNewGameserver()) {
                gsi = new GameServerInfo(packet.getId(), packet.getHexId(), gameserver);
                if (gameServerController.register(packet.getId(), gsi)) {
                    gameserver.attachGameServerInfo(gsi, packet.getPort(), packet.getHosts(), packet.getMaxPlayer());
                    //TODO: Register in DB
                    //gameServerTable.registerServerOnDB(gsi);
                } else {
                    log.error("ID {} is not available anymore.", packet.getId());
                    // someone took this ID meanwhile
                    gameserver.forceClose(LoginServerFailReason.REASON_ID_RESERVED.getCode());
                    return false;
                }
            } else {
                log.error("Wrong gameserver hexId: {}.", Arrays.toString(packet.getHexId()));
                gameserver.forceClose(LoginServerFailReason.REASON_WRONG_HEXID.getCode());
                return false;
            }
        }
        return true;
    }
}