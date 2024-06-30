package com.shnok.javaserver.security;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Arrays;

import static com.shnok.javaserver.config.Configuration.server;

@Log4j2
public class LoginCrypt {
    private static final byte[] STATIC_BLOWFISH_KEY = {
            (byte) 0x6b,
            (byte) 0x60,
            (byte) 0xcb,
            (byte) 0x5b,
            (byte) 0x82,
            (byte) 0xce,
            (byte) 0x90,
            (byte) 0xb1,
            (byte) 0xcc,
            (byte) 0x2b,
            (byte) 0x6c,
            (byte) 0x55,
            (byte) 0x6c,
            (byte) 0x6c,
            (byte) 0x6c,
            (byte) 0x6c
    };

    private static final NewCrypt _STATIC_CRYPT = new NewCrypt(STATIC_BLOWFISH_KEY);
    private NewCrypt _crypt = null;
    private boolean _static = true;

    /**
     * Method to initialize the blowfish cipher with dynamic key.
     * @param key the blowfish key to initialize the dynamic blowfish cipher with
     */
    public void setKey(byte[] key) {
        _crypt = new NewCrypt(key);
    }

    /**
     * Method to decrypt an incoming login client packet.
     * @param raw array with encrypted data
     * @param offset offset where the encrypted data is located
     * @param size number of bytes of encrypted data
     * @return true when checksum could be verified, false otherwise
     * @throws IOException the size is not multiple of blowfish block size or the raw array can't hold size bytes starting at offset due to its size
     */
    public boolean decrypt(byte[] raw, final int offset, final int size) throws IOException {
        if ((size % 8) != 0) {
            throw new IOException("size have to be multiple of 8");
        }
        if ((offset + size) > raw.length) {
            throw new IOException("raw array too short for size starting from offset");
        }

        _crypt.decrypt(raw, offset, size);
        return NewCrypt.verifyChecksum(raw, offset, size);
    }

    /**
     * Method to encrypt an outgoing packet to login client.<br>
     * Performs padding and resizing of data array.
     * @param raw array with plain data
     * @param offset offset where the plain data is located
     * @param size number of bytes of plain data
     * @return the new array size
     * @throws IOException packet is too long to make padding and add verification data
     */
    public int encrypt(byte[] raw, final int offset, int size) throws IOException {
        if (_static) {

            if(server.printCryptography()) {
                log.debug("INIT Packet encryption:");
                log.debug("CLEAR: " + Arrays.toString(raw));
            }

            NewCrypt.encXORPass(raw, offset, size, Rnd.nextInt());
            if(server.printCryptography()) {
                log.debug("XORED: " + Arrays.toString(raw));
            }

            _STATIC_CRYPT.crypt(raw, offset, size);
            if(server.printCryptography()) {
                log.debug("ENCRYPTED: " + Arrays.toString(raw));
            }

            _static = false;
        } else {
            NewCrypt.appendChecksum(raw, offset, size);
            _crypt.crypt(raw, offset, size);
        }

        return size;
    }
}

