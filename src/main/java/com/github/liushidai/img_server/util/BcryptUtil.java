package com.github.liushidai.img_server.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class BcryptUtil {
    /**
     * 加密密码
     *
     * @param password password
     * @return BCrypt处理后的密码
     */
    public static String encryptPassword(String password) {
        // 使用 BCrypt 算法对密码进行加密，将盐值和密码作为参数
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    /**
     * 检查密码是否匹配
     *
     * @param password     需要校验的密码
     * @param hashPassword 正确的密码
     * @return true 密码正确
     */
    public static boolean checkPassword(String password, String hashPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashPassword);
        return result.verified;
    }
}
