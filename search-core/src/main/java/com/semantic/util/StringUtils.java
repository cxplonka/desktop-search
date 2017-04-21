/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class StringUtils {

    static final byte[] HEX_CHAR_TABLE = {
        (byte) '0', (byte) '1', (byte) '2', (byte) '3',
        (byte) '4', (byte) '5', (byte) '6', (byte) '7',
        (byte) '8', (byte) '9', (byte) 'a', (byte) 'b',
        (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f'
    };

    public static String getHexString(byte[] raw) {
        byte[] hex = new byte[2 * raw.length];
        int index = 0;

        for (byte b : raw) {
            int v = b & 0xFF;
            hex[index++] = HEX_CHAR_TABLE[v >>> 4];
            hex[index++] = HEX_CHAR_TABLE[v & 0xF];
        }
        return new String(hex);
    }

    public static String intToString(int[] array) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            ret.append(Integer.toString(array[i])).append(' ');
        }
        return ret.toString().trim();
    }

    public static boolean isEmpty(String value) {
        return value == null || value.length() == 0 || value.trim().length() == 0;
    }
}
