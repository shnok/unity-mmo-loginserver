package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import com.shnok.javaserver.util.HexUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.crypto.Cipher;
import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;

import static com.shnok.javaserver.config.Configuration.server;

@Getter
@Log4j2
public class AuthRequestPacket extends ReceivablePacket {
    private final byte[] raw = new byte[128];
    private String account;
    private byte[] passHashBytes;

    public AuthRequestPacket(byte[] data, RSAPrivateKey privateKey) {
        super(data);

        byte[] decrypted;

        try {
            final Cipher rsaCipher = Cipher.getInstance(server.rsaPaddingMode());
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);

            decrypted = Arrays.copyOfRange(data, 1,  1 + 128);
            log.debug("Encrypted RSA: {}", Arrays.toString(decrypted));

            decrypted = rsaCipher.doFinal(decrypted, 0x00, 0x80);

            log.debug("Decrypted RSA: {}", Arrays.toString(decrypted));
        } catch (Exception ex) {
            log.warn("There has been an error trying to login!", ex);
            return;
        }

        int accountBlockLength = decrypted[0];
        log.debug("Account block length: " + accountBlockLength);
        int shaBlockLength = decrypted[accountBlockLength + 1];
        log.debug("Password block length: " + shaBlockLength);
        try {
            account = new String(decrypted, 1, accountBlockLength).trim().toLowerCase();
            passHashBytes = Arrays.copyOfRange(decrypted, accountBlockLength + 2, decrypted.length);
            log.debug("Password hash: {}", HexUtils.hexToString(passHashBytes));
        } catch (Exception ex) {
            log.warn("There has been an error parsing credentials!", ex);
        }
    }
}
