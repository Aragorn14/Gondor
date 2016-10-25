package com.scube.Gondor.Helpers.qr;

import android.util.Log;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Blowfish {

    private static Cipher cipher;
    private static SecretKeySpec secret_key;
    private static String qrText;
    private static String Key           = "HELLOTHIS IS SPARKK!@#$%^&*()";
    private static String cipher_key    = "Blowfish";

    //The main function which takes 2 args
    //args[0] = "decrypt" or "encrypt"
    //args[1] = data to decrypt or encrypt
    //It invokes data_modification which encrypts or decrypts data
    public static String main(String args[]) throws NoSuchAlgorithmException{
        try {
            return (execute_data_modification(args)); //Returns either encrypted data or decrypted data
        } catch (InvalidKeyException ex) {
            System.out.println("Error! " + ex.getMessage());
            return "BLOWFISH_ERROR";
        }
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a) sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) +
                    Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    //Decrypts the data which is in byte format
    private static byte[] decrypt_data(byte[] encrypted_text)
            throws InvalidKeyException {
        byte[] decrypted = null;
        try {
            //Initialize cipher with the generated secret key
            cipher.init(Cipher.DECRYPT_MODE, secret_key);

            //Do the decryption
            decrypted = cipher.doFinal(encrypted_text);
        } catch (Exception e) {
            System.out.println("Decryption Error! " + e.getMessage());
        }
        return decrypted;
    }

    //Encrypts the plain text data into byte format
    private static byte[] encrypt_data(String plain_text)
            throws InvalidKeyException {
        byte[] encrypted = null;
        try {
            //Re-initialize the cipher  for description
            cipher.init(Cipher.ENCRYPT_MODE, secret_key);

            //Do the encryption
            encrypted = cipher.doFinal(plain_text.getBytes());

        } catch (Exception e) {
            System.out.println("Encryption Error! " + e.getMessage());
        }
        return encrypted;
    }

    /*
        1. Generates secret key based on the Sparkk Key saved in Key
        2. Encryption: Invokes encrypt data and gets back encrypted data in byte format
                       then converts it into hex format which is the actual QR Code Text
        3. Decryption: Converts the scanned QR text which is in hex format and converts into
                       byte format.  And invokes decrypt function to get the data which is
                       then copied into QRtext (this is the plain text data)
    */
    private static String execute_data_modification(String args[])
            throws NoSuchAlgorithmException, InvalidKeyException {
        try {
            //Define the argument parameters
            String encryptOrDecrypt = args[0];
            String data = args[1];

            //Generate encryption key based on code cipherkey code word
            byte[] KeyData = Key.getBytes();
            secret_key = new SecretKeySpec(KeyData, "Blowfish");

            //Create Cipher based on the previous encryption key code
            cipher = Cipher.getInstance(cipher_key);

            if (encryptOrDecrypt.equals("encrypt")) {
                //Encrypt the text and store the data as bytes array
                byte[] encrypted_data = encrypt_data(data);

                Log.d("ENCRYPTED DATA", new String(encrypted_data));
                qrText = byteArrayToHex(encrypted_data);
                Log.d("ENCRYPTED DATA HUMAN FORMAT", qrText);
                Log.d("ENCRYPTED DATA LENGTH", ":" + qrText.length() + ":");

            } else if (encryptOrDecrypt.equals("decrypt")) {
                //Pass the data to be decrypted [To Do: try with getBytes in the future]
                byte[] encrypted_data = hexStringToByteArray(data);
                Log.d("DATA TO BE DECRYPTED", new String(encrypted_data));

                //Decrypt the the text back to its original form as bytes
                byte[] decrypted_data = decrypt_data(encrypted_data);
                qrText = new String(decrypted_data);
                Log.d("DECRYPTED PLAIN TEXT", qrText);

            }
        } catch (NoSuchPaddingException ex) {
            System.out.println("Error! " + ex.getMessage());
            qrText = "BLOWFISH_ERROR";
        }
        return qrText;
    }
}
