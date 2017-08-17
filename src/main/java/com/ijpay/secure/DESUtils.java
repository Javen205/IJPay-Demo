package com.ijpay.secure;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * des加密、解密
 */
public class DESUtils {

	// 指定DES加密解密所用的密钥
	private static Key key;

	/**
	 * 加密key为空, 默认为类名
	 */
	public DESUtils() {
		setkey(this.getClass().getName());
	}

	/**
	 * 设置加密key
	 * 
	 * @param keyStr
	 *            加密key值
	 */
	public DESUtils(String keyStr) {
		setkey(keyStr);
	}

	/**
	 * 设置加密的校验码
	 */
	private void setkey(String keyStr) {
		try {
			// L.cm 2015-01-20 将加密的密匙Base64
			// fix Caused by: java.security.InvalidKeyException: Wrong key size
			String desKey = Base64.encodeBase64String(keyStr.getBytes("UTF-8"));
			DESKeySpec objDesKeySpec = new DESKeySpec(desKey.getBytes("UTF-8"));
			SecretKeyFactory objKeyFactory = SecretKeyFactory.getInstance("DES");
			key = objKeyFactory.generateSecret(objDesKeySpec);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 对字符串进行DES加密，返回BASE64编码的加密字符串
	 * @param str
	 * @return
	 */
	public final String encryptString(String str) {
		byte[] bytes = str.getBytes();
		try {
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] encryptStrBytes = cipher.doFinal(bytes);
			return Base64.encodeBase64URLSafeString(encryptStrBytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 对BASE64编码的加密字符串进行解密，返回解密后的字符串
	 * @param str
	 * @return
	 */
	public final String decryptString(String str) {
		try {
			byte[] bytes = Base64.decodeBase64(str);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			bytes = cipher.doFinal(bytes);
			return new String(bytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		String keyStr="9cc9b11a7a0747d4b19d30a324a84dc5";
		System.out.println("keyStr:"+keyStr);
		DESUtils desUtils=new DESUtils(keyStr);
		String sourceStr="12345678";
		System.out.println("加密前的Str："+sourceStr);
		String encryptString = desUtils.encryptString(sourceStr);
		System.out.println("加密之后："+encryptString);
		 String decryptString = desUtils.decryptString(encryptString);
		System.out.println("解密之后："+decryptString);
	}
}
