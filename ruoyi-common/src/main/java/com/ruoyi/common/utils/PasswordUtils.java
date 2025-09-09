package com.ruoyi.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * 密码加密工具类
 * 
 * @author ruoyi
 */
public class PasswordUtils
{
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";
    private static final String SECRET_KEY = "RuoyiEmailMgr16!"; // 16字节密钥
    
    /**
     * 加密密码
     * 
     * @param password 明文密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String password)
    {
        if (password == null || password.trim().isEmpty()) {
            return null;
        }
        
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] encryptedBytes = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }
    
    /**
     * 解密密码
     * 
     * @param encryptedPassword 加密后的密码
     * @return 明文密码
     */
    public static String decryptPassword(String encryptedPassword)
    {
        if (encryptedPassword == null || encryptedPassword.trim().isEmpty()) {
            return null;
        }
        
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("密码解密失败", e);
        }
    }
    
    /**
     * 生成MD5哈希（用于不可逆加密）
     * 
     * @param input 输入字符串
     * @return MD5哈希值
     */
    public static String generateMD5(String input)
    {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5生成失败", e);
        }
    }
    
    /**
     * 验证密码是否匹配
     * 
     * @param plainPassword 明文密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean verifyPassword(String plainPassword, String encryptedPassword)
    {
        if (plainPassword == null || encryptedPassword == null) {
            return false;
        }
        
        try {
            String decryptedPassword = decryptPassword(encryptedPassword);
            return plainPassword.equals(decryptedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
