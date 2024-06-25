package com.shnok.javaserver.thread;

import com.shnok.javaserver.dto.Packet;
import com.shnok.javaserver.dto.internal.loginserverpackets.LoginServerFailPacket;
import com.shnok.javaserver.enums.GameServerState;
import com.shnok.javaserver.model.GameServerInfo;
import com.shnok.javaserver.security.NewCrypt;
import com.shnok.javaserver.service.GameServerListenerService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Log4j2
public class GameServerThread extends Thread {
    private InputStream in;
    private OutputStream out;
    private long lastEcho;
    private Timer watchDog;
    private Socket connection;
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    private NewCrypt blowfish;
    private GameServerState loginConnectionState = GameServerState.CONNECTED;
    private final String connectionIp;
    private GameServerInfo gameServerInfo;

    /**
     * Authed Clients on a GameServer
     */
    private final Set<String> accountsOnGameServer = ConcurrentHashMap.newKeySet();

    private String _connectionIPAddress;

    public GameServerThread(Socket con) {
        connection = con;
        connectionIp = con.getInetAddress().getHostAddress();

        try {
            in = connection.getInputStream();
            out = new BufferedOutputStream(connection.getOutputStream());
            log.debug("New gameserver connection: {}", connectionIp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        startReadingPackets();
    }

    private void startReadingPackets() {
        int packetType;
        int packetLength;

        try {
            for (; ; ) {
                packetType = in.read();
                packetLength = in.read();

                if (packetType == -1 || connection.isClosed()) {
                    log.warn("Connection was closed.");
                    break;
                }

                byte[] data = new byte[packetLength];
                data[0] = (byte) packetType;
                data[1] = (byte) packetLength;

                int receivedBytes = 0;
                int newBytes = 0;

                while ((newBytes != -1) && (receivedBytes < (packetLength - 2))) {
                    newBytes = in.read(data, 2, packetLength - 2);
                    receivedBytes = receivedBytes + newBytes;
                }

                handlePacket(data);
            }
        } catch (Exception e) {
            log.error("Exception while reading packets.");
        } finally {
            log.info("Gameserver {} connection closed.", connectionIp);
            disconnect();
        }
    }

    private void handlePacket(byte[] data) {
        // TODO: Handle gameserver packet
    }


    public void sendPacket(Packet packet) {
        //TODO: send packet to game server
    }

    public void disconnect() {
        try {
            GameServerListenerService.getInstance().removeGameServer(this);
            connection.close();
        } catch (IOException e) {
            log.error("Error while closing connection.", e);
        }
    }

    public void attachGameServerInfo(GameServerInfo gsi, int port, String[] hosts, int maxPlayers) {
        setGameServerInfo(gsi);
        gsi.setGameServerThread(this);
        gsi.setPort(port);
        setGameHosts(hosts);
        gsi.setMaxPlayers(maxPlayers);
        gsi.setAuthed(true);
    }

    public void setGameHosts(String[] hosts) {
        log.info("Updated game server {}[{}] IPs.", gameServerInfo.getName(), gameServerInfo.getId());

        gameServerInfo.clearServerAddresses();
        for (int i = 0; i < hosts.length; i += 2) {
            try {
                gameServerInfo.addServerAddress(hosts[i], hosts[i + 1]);
            } catch (Exception ex) {
                log.warn("There has been an error resolving host name {}!", hosts[i], ex);
            }
        }

        for (String s : gameServerInfo.getServerAddresses()) {
            log.info(s);
        }
    }

    public void forceClose(int reason) {
        sendPacket(new LoginServerFailPacket(reason));

        try {
            connection.close();
        } catch (IOException ex) {
            log.debug("Failed disconnecting banned server, server already disconnected.");
        }
    }

    public static boolean isBannedGameServerIP(String ipAddress) {
        return false;
    }

    public int getPlayerCount() {
        return accountsOnGameServer.size();
    }

    public boolean hasAccountOnGameServer(String account) {
        return accountsOnGameServer.contains(account);
    }

    public void addAccountOnGameServer(String account) {
        accountsOnGameServer.add(account);
    }

    public void removeAccountOnGameServer(String account) {
        accountsOnGameServer.remove(account);
    }

    public void setLastEcho(long lastEcho, Timer watchDog) {
        if(this.watchDog != null) {
            watchDog.stop();
        }

        this.lastEcho = lastEcho;
        this.watchDog = watchDog;
    }
}
