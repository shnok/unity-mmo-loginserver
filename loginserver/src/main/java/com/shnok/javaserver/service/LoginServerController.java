package com.shnok.javaserver.service;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.model.SessionKey;
import com.shnok.javaserver.security.Rnd;
import com.shnok.javaserver.security.ScrambledKeyPair;
import com.shnok.javaserver.thread.LoginClientThread;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.crypto.Cipher;
import java.net.Socket;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.ArrayList;
import java.util.List;

import static com.shnok.javaserver.config.Configuration.server;
import static java.security.spec.RSAKeyGenParameterSpec.F4;

@Log4j2
@Getter
public class LoginServerController {
    private final List<LoginClientThread> clients = new ArrayList<>();
    protected final ScrambledKeyPair[] keyPairs;
    protected byte[][] blowfishKeys;
    private static final int BLOWFISH_KEYS = 20;

    private static LoginServerController instance;
    public static LoginServerController getInstance() {
        if (instance == null) {
            instance = new LoginServerController();
        }
        return instance;
    }

    public LoginServerController() {
        log.info("Loading Login Controller...");

        keyPairs = new ScrambledKeyPair[10];

        try {
            final KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            final RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(1024, F4);
            keygen.initialize(spec);
            for (int i = 0; i < 10; i++) {
                keyPairs[i] = new ScrambledKeyPair(keygen.generateKeyPair());
            }

            testCipher((RSAPrivateKey) keyPairs[0].getPair().getPrivate());

            log.info("Cached 10 KeyPairs for RSA communication.");
        } catch (Exception ex) {
            log.error("There has been an error loading the key pairs!", ex);
        }

        // Store keys for blowfish communication
        generateBlowFishKeys();
    }

    private void testCipher(RSAPrivateKey key) throws Exception {
        // avoid worst-case execution, KenM
        Cipher rsaCipher = Cipher.getInstance(server.rsaPaddingMode());
        rsaCipher.init(Cipher.DECRYPT_MODE, key);
    }

    private void generateBlowFishKeys() {
        blowfishKeys = new byte[BLOWFISH_KEYS][16];

        for (int i = 0; i < BLOWFISH_KEYS; i++) {
            for (int j = 0; j < blowfishKeys[i].length; j++) {
                blowfishKeys[i][j] = (byte) (Rnd.nextInt(255) + 1);
            }
        }

        log.info("Stored {} keys for Blowfish communication.", blowfishKeys.length);
    }


    public void addClient(Socket socket) {
        LoginClientThread client = new LoginClientThread(socket);
        client.start();
        clients.add(client);
    }

    public LoginClientThread getClient(String login) {
        for (LoginClientThread client: clients) {
            if(client.getUsername() == null) {
                continue;
            }
            if(client.getUsername().equals(login)) {
                return client;
            }
        }

        return null;
    }

    public void removeClient(LoginClientThread s) {
        synchronized (clients) {
            clients.remove(s);
        }
    }

    public List<LoginClientThread> getAllClients() {
        return clients;
    }

    // Broadcast to everyone ignoring caller
    public void broadcast(SendablePacket packet, LoginClientThread current) {
        synchronized (clients) {
            for (LoginClientThread c : clients) {
                if (c.authenticated && c != current) {
                    c.sendPacket(packet);
                }
            }
        }
    }

   // Broadcast to everyone
   public void broadcast(SendablePacket packet) {
        synchronized (clients) {
            for (LoginClientThread c : clients) {
                if (c.authenticated) {
                    c.sendPacket(packet);
                }
            }
        }
    }

    public boolean userExists(String user) {
        synchronized (clients) {
            for (LoginClientThread c : clients) {
                if (c.authenticated) {
                    return c.getUsername().equals(user);
                }
            }

            return false;
        }
    }

    public ScrambledKeyPair getScrambledRSAKeyPair() {
        return keyPairs[Rnd.nextInt(10)];
    }

    public byte[] getBlowfishKey() {
        return blowfishKeys[(int) (Math.random() * BLOWFISH_KEYS)];
    }

    public SessionKey getNewSessionKey() {
        return new SessionKey(Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt());
    }

    public void getCharactersOnAccount(String account) {
        // TODO: get character list for account
    }
}
