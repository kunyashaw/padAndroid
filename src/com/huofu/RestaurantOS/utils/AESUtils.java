package com.huofu.RestaurantOS.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * 
 * Created by akwei on 8/6/14.
 */

public class AESUtils {

	private static final String transformation = "AES/ECB/PKCS5Padding";
	private Cipher encCipher;
	private Cipher decCipher;

	/**
	 * 创建AES实例
	 * @param key 加密解密使用的key
	 */
	public AESUtils(byte[] key) {

		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

		try {

			encCipher = Cipher.getInstance(transformation);

			encCipher.init(Cipher.ENCRYPT_MODE, secretKey);

			decCipher = Cipher.getInstance(transformation);// 创建密码器

			decCipher.init(Cipher.DECRYPT_MODE, secretKey);// 初始化

		}

		catch (Exception e) {

			throw new RuntimeException(e);
		}
	}

	
	/**
	 * 加密
	 * @param data 加密后的数据
	 * @return
	 */
	public byte[] encrypt(byte[] data) {
		try {
			return this.encCipher.doFinal(data);
		}

		catch (Exception e) {

			throw new RuntimeException(e);

		}

	}

	
	/**
	 * 解密
	 * @param data 解密后的明文数据
	 * @return
	 */
	public byte[] decrypt(byte[] data) {

		try {

			return this.decCipher.doFinal(data);

		}

		catch (Exception e) {

			throw new RuntimeException(e);

		}

	}
}