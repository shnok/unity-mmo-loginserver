package com.shnok.javaserver.util;

import java.math.BigInteger;
import java.util.Arrays;

public class HexUtils {
    public static String hexToString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0'); // Pad with leading zero if necessary
            }
            hexString.append(hex);
        }

        System.out.println(Arrays.toString(bytes));
        System.out.println(hexString.toString());
        return hexString.toString();
    }

    public static byte[] stringToHex(String hexString) {
        int len = hexString.length();
        byte[] byteArray = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i+1), 16));
        }
        return byteArray;
    }
}
