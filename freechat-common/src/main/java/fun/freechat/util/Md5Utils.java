package fun.freechat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SuppressWarnings("unused")
public class Md5Utils {
    private static final Logger log = LoggerFactory.getLogger(Md5Utils.class);

    public static String md5(String data) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.error("Find MD5 Algorithm failed!", e);
            return data;
        }

        byte[] md5Bytes = md5.digest(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append('0');
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString().toLowerCase();
    }
}
