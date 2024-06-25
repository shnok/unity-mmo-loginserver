package com.shnok.javaserver.thread;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.AccountKickedReason;
import com.shnok.javaserver.enums.LoginClientState;
import com.shnok.javaserver.enums.LoginFailReason;
import com.shnok.javaserver.enums.packettypes.ServerPacketType;
import com.shnok.javaserver.model.SessionKey;
import com.shnok.javaserver.security.LoginCrypt;
import com.shnok.javaserver.security.ScrambledKeyPair;
import com.shnok.javaserver.service.LoginServerController;
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

@Getter
@Setter
@Log4j2
public class LoginClientThread extends Thread {
    private final Socket connection;
    private final String connectionIp;
    public boolean authenticated;
    private InputStream in;
    private OutputStream out;
    private String username;
    private boolean clientReady = false;
    private long lastEcho;
    private Timer watchDog;
    private LoginClientState loginClientState;
    private final LoginCrypt loginCrypt;
    private final ScrambledKeyPair scrambledPair;
    private final byte[] blowfishKey;
    private int accessLevel;
    private int lastGameserver;
    private SessionKey sessionKey;

    public LoginClientThread(Socket con) {
        connection = con;
        connectionIp = con.getInetAddress().getHostAddress();
        scrambledPair = LoginServerController.getInstance().getScrambledRSAKeyPair();
        blowfishKey = LoginServerController.getInstance().getBlowfishKey();
        loginCrypt = new LoginCrypt();
        loginCrypt.setKey(blowfishKey);

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

                handlePacket(data);
            }
        } catch (Exception e) {
            log.error("Exception while reading client packets.");
        } finally {
            log.info("User {} disconnected", connectionIp);
            disconnect();
        }
    }

    public void close(AccountKickedReason failReason) {
        //TODO: Send kick reason
        disconnect();
    }

    public void close(LoginFailReason failReason) {
        //TODO: Send fail reason
        disconnect();
    }

    public void disconnect() {
        try {
            removeSelf();
            connection.close();
        } catch (IOException e) {
            log.error("Error while closing connection.", e);
        }
    }

    public boolean sendPacket(SendablePacket packet) {
            ServerPacketType packetType = ServerPacketType.fromByte(packet.getType());
            if(packetType != ServerPacketType.Ping) {
                log.debug("Sent packet: {}", packetType);
            }

        try {
            synchronized (out) {
                out.write(packet.getLength() & 0xff);
                out.write((packet.getLength() >> 8) & 0xff);
                for (byte b : packet.getData()) {
                    out.write(b & 0xFF);
                }
                out.flush();
            }

            return true;
        } catch (IOException e) {
            log.warn("Trying to send packet to a closed game client.");
        }

        return false;
    }

    void handlePacket(byte[] data) {
        //ThreadPoolManagerService.getInstance().handlePacket(new ClientPacketHandlerThread(this, data));
    }

    public void setLastEcho(long lastEcho, Timer watchDog) {
        if(this.watchDog != null) {
            watchDog.stop();
        }

        this.lastEcho = lastEcho;
        this.watchDog = watchDog;
    }

    void authenticate() {
        log.debug("Authenticating new player.");

    }

    private void removeSelf() {
        if (authenticated) {
            authenticated = false;

            if(!clientReady) {
                return;
            }
        }

        LoginServerController.getInstance().removeClient(this);
        this.interrupt();
    }

    public RSAPrivateKey getRSAPrivateKey() {
        return (RSAPrivateKey) scrambledPair.getPair().getPrivate();
    }
}
