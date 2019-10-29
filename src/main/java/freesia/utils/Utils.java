package freesia.utils;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    public static String computeHash(String datum){
        final MessageDigest messageDigest;
        String result = "";
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(datum.getBytes(Charset.forName("UTF8")));
            final byte[] resultByte = messageDigest.digest();
            result = new String(Hex.encodeHex(resultByte));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }
}
