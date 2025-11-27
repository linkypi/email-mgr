package com.ruoyi.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA加密解密工具类
 * 用于前端密码加密传输
 * 
 * @author ruoyi
 */
public class RsaUtils
{
    private static final Logger log = LoggerFactory.getLogger(RsaUtils.class);
    
    /**
     * RSA私钥（与前端jsencrypt.js中的私钥保持一致）
     * 密钥对生成地址：http://web.chacuo.net/netrsakeypair
     */
    private static final String PRIVATE_KEY = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAqhHyZfSsYourNxaY\n" +
            "7Nt+PrgrxkiA50efORdI5U5lsW79MmFnusUA355oaSXcLhu5xxB38SMSyP2KvuKN\n" +
            "PuH3owIDAQABAkAfoiLyL+Z4lf4Myxk6xUDgLaWGximj20CUf+5BKKnlrK+Ed8gA\n" +
            "kM0HqoTt2UZwA5E2MzS4EI2gjfQhz5X28uqxAiEA3wNFxfrCZlSZHb0gn2zDpWow\n" +
            "cSxQAgiCstxGUoOqlW8CIQDDOerGKH5OmCJ4Z21v+F25WaHYPxCFMvwxpcw99Ecv\n" +
            "DQIgIdhDTIqD2jfYjPTY8Jj3EDGPbH2HHuffvflECt3Ek60CIQCFRlCkHpi7hthh\n" +
            "YhovyloRYsM+IS9h/0BzlEAuO0ktMQIgSPT3aFAgJYwKpqRYKlLDVcflZFCKY7u3\n" +
            "UP8iWi1Qw0Y=";
    
    /**
     * RSA公钥（与前端jsencrypt.js中的公钥保持一致）
     */
    private static final String PUBLIC_KEY = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKoR8mX0rGKLqzcWmOzbfj64K8ZIgOdH\n" +
            "nzkXSOVOZbFu/TJhZ7rFAN+eaGkl3C4buccQd/EjEsj9ir7ijT7h96MCAwEAAQ==";
    
    /**
     * RSA算法
     */
    private static final String ALGORITHM = "RSA";
    
    /**
     * RSA加密填充方式
     */
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    
    /**
     * 获取私钥
     * 
     * @return 私钥对象
     * @throws Exception 异常
     */
    private static PrivateKey getPrivateKey() throws Exception
    {
        // 移除换行符和空格
        String privateKeyStr = PRIVATE_KEY.replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }
    
    /**
     * 获取公钥
     * 
     * @return 公钥对象
     * @throws Exception 异常
     */
    private static PublicKey getPublicKey() throws Exception
    {
        // 移除换行符和空格
        String publicKeyStr = PUBLIC_KEY.replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }
    
    /**
     * RSA解密
     * 
     * @param encryptedData 加密后的数据（Base64编码）
     * @return 解密后的原始数据
     */
    public static String decrypt(String encryptedData)
    {
        if (StringUtils.isEmpty(encryptedData))
        {
            log.warn("RSA解密失败：加密数据为空");
            return null;
        }
        
        try
        {
            PrivateKey privateKey = getPrivateKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            
            // Base64解码
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            // RSA解密
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            // 转换为字符串
            return new String(decryptedBytes, java.nio.charset.StandardCharsets.UTF_8);
        }
        catch (Exception e)
        {
            log.error("RSA解密失败：{}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * RSA加密（用于测试或后端加密场景）
     * 
     * @param data 原始数据
     * @return 加密后的数据（Base64编码）
     */
    public static String encrypt(String data)
    {
        if (StringUtils.isEmpty(data))
        {
            log.warn("RSA加密失败：数据为空");
            return null;
        }
        
        try
        {
            PublicKey publicKey = getPublicKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            
            // RSA加密
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            // Base64编码
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }
        catch (Exception e)
        {
            log.error("RSA加密失败：{}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 获取RSA公钥（用于前端动态获取）
     * 
     * @return RSA公钥字符串
     */
    public static String getPublicKeyString()
    {
        return PUBLIC_KEY;
    }
}



