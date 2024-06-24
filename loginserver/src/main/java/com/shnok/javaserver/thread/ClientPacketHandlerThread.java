package com.shnok.javaserver.thread;

import com.shnok.javaserver.db.entity.DBAccountInfo;
import com.shnok.javaserver.dto.external.clientpackets.AuthRequestPacket;
import com.shnok.javaserver.dto.external.serverpackets.*;
import com.shnok.javaserver.enums.*;
import com.shnok.javaserver.service.LoginServerController;
import com.shnok.javaserver.service.db.AccountInfoTableService;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.shnok.javaserver.config.Configuration.server;
import static java.nio.charset.StandardCharsets.UTF_8;

@Log4j2
public class ClientPacketHandlerThread extends Thread {
    private final LoginClientThread client;
    private final byte[] data;

    public ClientPacketHandlerThread(LoginClientThread client, byte[] data) {
        this.client = client;
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
                onReceiveAuth(data, client.getRSAPrivateKey());
                break;
        }
    }

    private void onReceiveEcho() {
        client.sendPacket(new PingPacket());

        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (System.currentTimeMillis() - client.getLastEcho() >= server.serverConnectionTimeoutMs()) {
                    log.info("User connection timeout.");
                    client.disconnect();
                }
            }
        });
        timer.setRepeats(false);
        timer.start();

        client.setLastEcho(System.currentTimeMillis(), timer);
    }

    private void onReceiveAuth(byte[] data, RSAPrivateKey privateKey) {
        AuthRequestPacket packet = new AuthRequestPacket(data, privateKey);
        String username = packet.getUsername();
        String password = packet.getUsername();

        InetAddress clientAddr = client.getConnection().getInetAddress();

        DBAccountInfo accountInfo;
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA");
            final byte[] raw = password.getBytes(UTF_8);
            final String hashBase64 = Base64.getEncoder().encodeToString(md.digest(raw));
            accountInfo = AccountInfoTableService.getInstance().getAccountInfo(username);

            if (accountInfo != null) {
                if(!accountInfo.getPassHash().equals(hashBase64)) {
                    // TODO: CLOSE CLIENT WITH LoginFailReason.REASON_USER_OR_PASS_WRONG
                    client.disconnect();
                    return;
                }

            } else if (server.autoCreateAccount()) {
                accountInfo = new DBAccountInfo();
                accountInfo.setLogin(username);
                accountInfo.setPassHash(hashBase64);
                accountInfo.setLastActive(System.currentTimeMillis());
                accountInfo.setLastIp(client.getConnectionIp());
                AccountInfoTableService.getInstance().createAccount(accountInfo);
                log.info("Autocreated account {}.", username);
            } else {
                // TODO: CLOSE CLIENT WITH LoginFailReason.REASON_USER_OR_PASS_WRONG
                client.disconnect();
                return;
            }
        } catch(NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }

        AuthLoginResult result = tryCheckinAccount(accountInfo);

        switch (result) {
            case AUTH_SUCCESS:

                client.setUsername(accountInfo.getLogin());
                client.setLoginClientState(LoginClientState.AUTHED_LOGIN);
                client.setSessionKey(LoginServerController.getInstance().getNewSessionKey());
                LoginServerController.getInstance().getCharactersOnAccount(accountInfo.getLogin());

                if (server.showLicense()) {
                    client.sendPacket(new LoginOkPacket(client.getSessionKey()));
                } else {
                    client.sendPacket(new ServerListPacket());
                }

                break;

            case INVALID_PASSWORD:
                client.close(LoginFailReason.REASON_USER_OR_PASS_WRONG);
                break;
            case ACCOUNT_INACTIVE:
                client.close(LoginFailReason.REASON_INACTIVE);
                break;
            case ACCOUNT_BANNED:
                client.close(AccountKickedReason.REASON_PERMANENTLY_BANNED);
                break;
            case ALREADY_ON_LS:
                LoginClientThread oldClient = LoginServerController.getInstance().getClient(accountInfo.getLogin());
                if (oldClient != null) {
                    // kick the other client
                    oldClient.close(LoginFailReason.REASON_ACCOUNT_IN_USE);
                }
                // kick also current client
                client.close(LoginFailReason.REASON_ACCOUNT_IN_USE);
                break;
            case ALREADY_ON_GS:
//                GameServerInfo gsi = lc.getAccountOnGameServer(info.getLogin());
//                if (gsi != null) {
//                    client.close(LoginFailReason.REASON_ACCOUNT_IN_USE);
//
//                    // kick from there
//                    if (gsi.isAuthed()) {
//                        gsi.getGameServerThread().kickPlayer(info.getLogin());
//                    }
//                }
            break;
        }
    }

    public AuthLoginResult tryCheckinAccount(DBAccountInfo info) {
        if (info.getAccessLevel() < 0) {
            if (info.getAccessLevel() == server.accountInactiveLevel()) {
                return AuthLoginResult.ACCOUNT_INACTIVE;
            }
            return AuthLoginResult.ACCOUNT_BANNED;
        }

        AuthLoginResult ret = AuthLoginResult.INVALID_PASSWORD;
        // check auth
        if (canCheckIn(info)) {
            // login was successful, verify presence on game servers
            ret = AuthLoginResult.ALREADY_ON_GS;
            if (!isAccountInAnyGameServer(info.getLogin())) {
                // account isn't on any GS verify LS itself
                ret = AuthLoginResult.ALREADY_ON_LS;

                if (LoginServerController.getInstance().getClient(info.getLogin()) == null) {
                    ret = AuthLoginResult.AUTH_SUCCESS;
                }
            }
        }

        return ret;
    }

    public boolean isAccountInAnyGameServer(String account) {
        // TODO: Check is account logges in on GS
//        Collection<GameServerInfo> serverList = GameServerTable.getInstance().getRegisteredGameServers().values();
//        for (GameServerInfo gsi : serverList) {
//            GameServerThread gst = gsi.getGameServerThread();
//            if ((gst != null) && gst.hasAccountOnGameServer(account)) {
//                return true;
//            }
//        }
        return false;
    }

    public boolean canCheckIn(DBAccountInfo info) {
        try {
            // TODO: Check at ip whitelist/ban list

            client.setAccessLevel(info.getAccessLevel());
            client.setLastGameserver(info.getLastServer());

            info.setLastIp(client.getConnectionIp());
            info.setLastActive(System.currentTimeMillis());

            AccountInfoTableService.getInstance().updateAccount(info);

            return true;
        } catch (Exception ex) {
            log.warn("There has been an error logging in!", ex);
            return false;
        }
    }
}
