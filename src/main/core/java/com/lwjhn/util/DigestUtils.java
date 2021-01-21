package com.lwjhn.util;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: lwjhn
 * @Date: 2020-11-30
 * @Description: com.lwjhn.util
 * @Version: 1.0
 */
public class DigestUtils {

    public static String digest(File file) throws Exception {
        InputStream is = null;
        try {
            return digest(is = new FileInputStream(file));
        } catch (Exception e) {
            throw e;
        } finally {
            AutoCloseableBase.close(is);
        }
    }

    public static String digest(File file, String algorithm) throws IOException, NoSuchAlgorithmException {
        InputStream is = null;
        try {
            return digest(is = new FileInputStream(file), algorithm);
        } catch (Exception e) {
            throw e;
        } finally {
            AutoCloseableBase.close(is);
        }
    }

    public static String digest(InputStream inputStream) throws IOException, NoSuchAlgorithmException {
        return digest(inputStream, MessageDigest.getInstance(ALGORITHM));
    }

    public static String digest(InputStream inputStream, String algorithm) throws IOException, NoSuchAlgorithmException {
        return digest(inputStream, MessageDigest.getInstance(algorithm));
    }

    public static String digest(InputStream inputStream, MessageDigest digest) throws IOException {
        byte[] buffer = new byte[2048];
        int read = inputStream.read(buffer);
        while (read > -1) {
            digest.update(buffer, 0, read);
            read = inputStream.read(buffer);
        }
        return encodeHexString(digest.digest());
    }

    public static String copyFileAndDigest(File input, File output) throws Exception {
        InputStream is = null;
        OutputStream os = null;
        try {
            return copyFileAndDigest(is = new FileInputStream(input), os = new FileOutputStream(output));
        } catch (Exception e) {
            throw e;
        } finally {
            AutoCloseableBase.close(is, os);
        }
    }

    public static String copyFileAndDigest(File input, File output, String algorithm) throws Exception {
        InputStream is = null;
        OutputStream os = null;
        try {
            return copyFileAndDigest(is = new FileInputStream(input), os = new FileOutputStream(output), algorithm);
        } catch (Exception e) {
            throw e;
        } finally {
            AutoCloseableBase.close(is, os);
        }
    }

    public static String copyFileAndDigest(InputStream inputStream, OutputStream output) throws Exception {
        return copyFileAndDigest(inputStream, output, ALGORITHM);
    }

    public static String copyFileAndDigest(InputStream inputStream, OutputStream output, String algorithm) throws Exception {
        return copyFileAndDigest(inputStream, output, MessageDigest.getInstance(algorithm));
    }

    public static String copyFileAndDigest(InputStream inputStream, OutputStream output, MessageDigest digest) throws IOException {
        byte[] buffer = new byte[2048];
        int read = inputStream.read(buffer);
        while (read > -1) {
            digest.update(buffer, 0, read);
            output.write(buffer, 0, read);
            read = inputStream.read(buffer);
        }
        output.flush();
        return encodeHexString(digest.digest());
    }

    protected static String encodeHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    public static final String ALGORITHM = "MD5";
    private static char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
}
