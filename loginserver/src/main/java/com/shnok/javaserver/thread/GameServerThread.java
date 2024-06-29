package com.shnok.javaserver.thread;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.dto.internal.loginserverpackets.InitLSPacket;
import com.shnok.javaserver.dto.internal.loginserverpackets.LoginServerFailPacket;
import com.shnok.javaserver.dto.internal.loginserverpackets.RequestCharactersPacket;
import com.shnok.javaserver.enums.GameServerState;
import com.shnok.javaserver.enums.packettypes.internal.LoginServerPacketType;
import com.shnok.javaserver.model.GameServerInfo;
import com.shnok.javaserver.security.NewCrypt;
import com.shnok.javaserver.service.GameServerController;
import com.shnok.javaserver.service.GameServerListenerService;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.util.ServerNameDAO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Log4j2
public class GameServerThread extends Thread {
    private InputStream in;
    private OutputStream out;
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

        KeyPair pair = GameServerController.getInstance().getKeyPair();
        privateKey = (RSAPrivateKey) pair.getPrivate();
        publicKey = (RSAPublicKey) pair.getPublic();
        blowfish = new NewCrypt("_;v.]05-31!|+-%xT!^[$\00");
        setName(getClass().getSimpleName() + "-" + Thread.currentThread().getId() + "@" + connectionIp);
    }

    @Override
    public void run() {
        startReadingPackets();
    }

    private void startReadingPackets() {
        int lengthHi;
        int lengthLo;
        int length;

        try {
            sendPacket(new InitLSPacket(publicKey.getModulus().toByteArray()));

            for (; ; ) {
                lengthLo = in.read();
                lengthHi = in.read();
                length = (lengthHi * 256) + lengthLo;

                if ((lengthHi < 0) || connection.isClosed()) {
                    log.warn("Gameserver terminated the connection!");
                    break;
                }

                byte[] data = new byte[length];

                int receivedBytes = 0;
                int newBytes = 0;
                while ((newBytes != -1) && (receivedBytes < (length))) {
                    newBytes = in.read(data, 0, length);
                    receivedBytes = receivedBytes + newBytes;
                }

                handlePacket(data);
            }
        } catch (Exception e) {
        } finally {
            log.info("Gameserver {} connection closed.", connectionIp);
            disconnect();
        }
    }

    private void handlePacket(byte[] data) {
        ThreadPoolManagerService.getInstance().handlePacket(new GameServerPacketHandler(this, data));
    }

    public boolean sendPacket(SendablePacket packet) {
        LoginServerPacketType packetType = LoginServerPacketType.fromByte(packet.getType());
        log.debug("Sent packet: {}", packetType);

        NewCrypt.appendChecksum(packet.getData());

        log.debug("---> Clear packet {} : {}", packet.getData().length, Arrays.toString(packet.getData()));
        blowfish.crypt(packet.getData(), 0, packet.getData().length);
        log.debug("---> Encrypted packet {} : {}", packet.getData().length, Arrays.toString(packet.getData()));

        try {
            synchronized (out) {
                out.write((byte)(packet.getData().length) & 0xff);
                out.write((byte)((packet.getData().length) >> 8) & 0xff);

                for (byte b : packet.getData()) {
                    out.write(b & 0xFF);
                }
                out.flush();
            }

            return true;
        } catch (IOException e) {
            log.warn("Trying to send packet to a gameserver connection.");
        }

        return false;
    }

    public void disconnect() {
        try {
            if (gameServerInfo.isAuthed()) {
                gameServerInfo.setDown();

                log.info("Server {}[{}] is now disconnected.", ServerNameDAO.getServer(gameServerInfo.getId()),
                        gameServerInfo.getId());
            }

            GameServerListenerService.getInstance().removeGameServer(this);

            connection.close();
        } catch (IOException e) {
            log.error("Error while closing connection.", e);
        }
    }

    public void attachGameServerInfo(GameServerInfo gsi, int port, String[] hosts, int maxPlayers) {
        log.debug("Attaching gameserver with ID: {}.", gsi.getId());

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

        disconnect();
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

    public void setBlowfish(NewCrypt newCrypt) {
        log.info("New BlowFish key received, Blowfish Engine initialized.");
        blowfish = newCrypt;
    }

    public void setLoginConnectionState(GameServerState state) {
        log.info("New gameserver connection state: {}", state);
        loginConnectionState = state;
    }

    public int getServerId() {
        return gameServerInfo.getId();
    }

    public String getServerName() {
        return gameServerInfo.getName();
    }

    public void requestCharacters(String account) {
        sendPacket(new RequestCharactersPacket(account));
    }
}
