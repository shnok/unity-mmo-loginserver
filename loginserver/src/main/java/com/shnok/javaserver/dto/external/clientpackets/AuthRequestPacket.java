package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.thread.LoginClientThread;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.crypto.Cipher;
import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;

@Getter
@Log4j2
public class AuthRequestPacket extends ClientPacket {
    private final byte[] raw = new byte[128];

    private String username;
    private String password;

    public AuthRequestPacket(byte[] data, RSAPrivateKey privateKey) {
        super(data);

        byte[] decrypted;

        try {
            final Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            decrypted = rsaCipher.doFinal(Arrays.copyOfRange(data, 0, data.length), 0x00, 0x80);
        } catch (Exception ex) {
            log.warn("There has been an error trying to login!", ex);
            return;
        }

        try {
            username = new String(decrypted, 0, 14).trim().toLowerCase();
            password = new String(decrypted, 14, 16).trim();
        } catch (Exception ex) {
            log.warn("There has been an error parsing credentials!", ex);
        }
    }
}
