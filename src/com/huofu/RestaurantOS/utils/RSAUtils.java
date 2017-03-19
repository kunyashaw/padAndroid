package com.huofu.RestaurantOS.utils;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtils {

    public static final String KEY_ALGORITHM = "RSA";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    private static String ALGORITHM = "RSA/ECB/PKCS1Padding";

    public static final int KEYSIZE = 1024;

    private byte[] publicKeyBytes;

    private byte[] privateKeyBytes;

    private byte[] modulusBytes;

    private Cipher encryptCipher;

    private Cipher decryptCipher;

    public RSAUtils() {
    }

    public RSAUtils(byte[] publicKeyBytes, byte[] privateKeyBytes,
                   byte[] modulusBytes) {
        this.publicKeyBytes = publicKeyBytes;
        this.privateKeyBytes = privateKeyBytes;
        this.modulusBytes = modulusBytes;
        try {
            if (this.publicKeyBytes != null) {
                Key publicKey = buildPublicKey(this.publicKeyBytes);
                this.encryptCipher = Cipher.getInstance(ALGORITHM);
                this.encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            }
            if (this.privateKeyBytes != null) {
                Key privateKey = buildPrivateKey(this.privateKeyBytes);
                this.decryptCipher = Cipher.getInstance(ALGORITHM);
                this.decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator
                .getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        this.publicKeyBytes = publicKey.getEncoded();
        this.privateKeyBytes = privateKey.getEncoded();
        this.modulusBytes = publicKey.getModulus().toByteArray();
        this.encryptCipher = Cipher.getInstance(ALGORITHM);
        this.encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        this.decryptCipher = Cipher.getInstance(ALGORITHM);
        this.decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
    }

    public byte[] getPublicKeyBytes() {
        return publicKeyBytes;
    }

    public byte[] getPrivateKeyBytes() {
        return privateKeyBytes;
    }

    public byte[] getModulusBytes() {
        return modulusBytes;
    }

    public static RSAUtils createRSAUtilWithRandomKeys() throws Exception {
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        kpg.initialize(KEYSIZE, new SecureRandom());
        KeyPair kp = kpg.generateKeyPair();
        Key publicKey = kp.getPublic();
        Key privateKey = kp.getPrivate();
        RSAPublicKey rsp = (RSAPublicKey) kp.getPublic();
        BigInteger bint = rsp.getModulus();
        RSAUtils rsaUtil = new RSAUtils(publicKey.getEncoded(),
                                      privateKey.getEncoded(), bint.toByteArray());
        return rsaUtil;
    }

    public byte[] encrypt(byte[] data) {
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        byte[] encryptedData = new byte[0];
        try {
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = this.encryptCipher.doFinal(data, offSet,
                                                       MAX_ENCRYPT_BLOCK);
                }
                else {
                    cache = this.encryptCipher.doFinal(data, offSet, inputLen
                            - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            encryptedData = out.toByteArray();
            return encryptedData;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                out.close();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public byte[] decrypt(byte[] data) {
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        try {
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = this.decryptCipher
                            .doFinal(data, offSet, MAX_DECRYPT_BLOCK);
                }
                else {
                    cache = this.decryptCipher
                            .doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            return decryptedData;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                out.close();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private PublicKey buildPublicKey(byte[] bytes) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    private PrivateKey buildPrivateKey(byte[] bytes) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    public byte[] sign(byte[] bytes, String algorithm) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(this.privateKeyBytes);
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(priKey);
            signature.update(bytes);
            byte[] data = signature.sign();
            return data;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkSign(byte[] bytes, byte[] signData, String algorithm) {
        try {
            PublicKey pubKey = buildPublicKey(this.publicKeyBytes);
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(pubKey);
            signature.update(bytes);
            return signature.verify(signData);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

