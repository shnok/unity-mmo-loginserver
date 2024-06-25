package com.shnok.javaserver.thread;

import com.shnok.javaserver.enums.GameServerState;
import com.shnok.javaserver.model.GameServerInfo;
import com.shnok.javaserver.security.NewCrypt;
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

    private NewCrypt _blowfish;

    private GameServerState _loginConnectionState = GameServerState.CONNECTED;

    private final String connectionIp;

    private GameServerInfo _gsi;

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
            log.debug("New connection: {}" + connectionIp);
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

                //handlePacket(data);
            }
        } catch (Exception e) {
            log.error("Exception while reading packets.");
        } finally {
            log.info("User {} disconnected", connectionIp);
          //  disconnect();
        }
    }

    public int getPlayerCount() {
        return accountsOnGameServer.size();
    }
}
