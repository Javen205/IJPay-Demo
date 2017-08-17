package com.ijpay.secure;


import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES工具类，密钥必须是16位字符串
 */
public class AESUtils {

	/**偏移量,必须是16位字符串*/
    private static final String IV_STRING = "16-Bytes--String";

    /**
     * 默认的密钥
     */
    public static final String DEFAULT_KEY = "1bd83b249a414036";

    /**
     * 产生随机密钥(这里产生密钥必须是16位)
     */
    public static String generateKey() {
        String key = UUID.randomUUID().toString();
        key = key.replace("-", "").substring(0, 16);// 替换掉-号
        return key;
    }

    public static String encryptData(String key, String content) {
        byte[] encryptedBytes = new byte[0];
        try {
            byte[] byteContent = content.getBytes("UTF-8");
            // 注意，为了能与 iOS 统一
            // 这里的 key 不可以使用 KeyGenerator、SecureRandom、SecretKey 生成
            byte[] enCodeFormat = key.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
            byte[] initParam = IV_STRING.getBytes();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
            // 指定加密的算法、工作模式和填充方式
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            encryptedBytes = cipher.doFinal(byteContent);
            // 同样对加密后数据进行 base64 编码
//            return Base64.encodeBase64String(encryptedBytes);
            return Base64Utils.encode(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptData(String key, String content) {
        try {
            // base64 解码
            byte[] encryptedBytes = Base64Utils.decode(content);
//            byte[] encryptedBytes = Base64.decodeBase64(content);
            byte[] enCodeFormat = key.getBytes();
            SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, "AES");
            byte[] initParam = IV_STRING.getBytes();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            byte[] result = cipher.doFinal(encryptedBytes);
            return new String(result, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void main(String[] args) {
    	try {
    		
    		Map<String, String> keys = RSAUtils.getKeys();
    		String publicKey = keys.get("publicKey");
    		String privateKey = keys.get("privateKey");
    		
			String key = generateKey();
			System.out.println("aes密钥："+key);
			String plainText = "I am Javen -By 我是Javen";
			plainText = encryptData(key, plainText);
			System.out.println("aes加密后: " + plainText );
			plainText = decryptData(key, plainText);
			System.out.println("aes解密后: " + plainText );
			String encrypt = RSAUtils.encryptByPublicKey(key,publicKey);
			System.out.println("RSA加密："+encrypt);
			System.out.println("RSA解密："+RSAUtils.decryptByPrivateKey(encrypt,privateKey));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
}
