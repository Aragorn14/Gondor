package com.scube.Gondor.Util;

import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by Srikanth on 4/26/2015.
 */
public class PasswordUtils {

    public static String computeSHAHash(String password) {
        String SHAHash = null;
        MessageDigest mdSha1 = null;
        Log.d("PasswordUtils","Plaintext password is : " + password);

        try
        {
            mdSha1 = MessageDigest.getInstance("SHA-1");
            mdSha1.update(password.getBytes("ASCII"));
            byte[] data = mdSha1.digest();
            SHAHash=convertToHex(data);
            Log.d("PasswordUtils","Hashed password is    : " + SHAHash);
        } catch (NoSuchAlgorithmException e1) {
            Log.e("PasswordUtils", "Error initializing SHA1 message digest");
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SHAHash;
    }

    private static String convertToHex(byte[] data) throws IOException {
        StringBuilder sb = new StringBuilder();
        String hex = Base64.encodeToString(data, 0, data.length, 0);
        sb.append(hex);
        return sb.toString();
    }

    // Return a random 15 digit string consisting of only the numbers 0 to 9
    public static String randomString(int length) {
        Random random = new Random();
        StringBuilder tmp = new StringBuilder();
        if (length < 1)
            throw new IllegalArgumentException("length < 1: " + length);
        char[] buf = new char[length];
        for (char ch = '0'; ch <= '9'; ++ch)
            tmp.append(ch);
        char[] symbols = tmp.toString().toCharArray();

        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }
}
