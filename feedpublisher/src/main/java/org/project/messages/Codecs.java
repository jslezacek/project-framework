package org.project.messages;

import java.nio.charset.StandardCharsets;

public class Codecs {
    public static Integer decodeUInt16(byte[] rawBytes) {
        return ((rawBytes[0] & 0xFF) << 8) | (rawBytes[1] & 0xFF);
    }

    public static byte[] encodeUInt16(Integer inputInt) {
        byte [] encodedInt = new byte[2];
        // big-endian
        encodedInt[0] = (byte)(inputInt >> 8 & 0xFF);
        encodedInt[1] = (byte)(inputInt & 0xFF);
        return encodedInt;
    }

    public static String decodeAscii(byte[] rawBytes) {
        return new String(rawBytes, StandardCharsets.US_ASCII);
    }
    public static byte[] encodeAscii(String input_string) {
        return input_string.getBytes(StandardCharsets.US_ASCII);
    }
}