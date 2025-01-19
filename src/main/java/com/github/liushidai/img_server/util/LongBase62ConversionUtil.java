package com.github.liushidai.img_server.util;

import java.util.Random;

public class LongBase62ConversionUtil {

    private static final int base62Length = longToBase62(562949953421311L).length();
    private static final String BASE_62_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789" ;
    private static final Random RANDOM = new Random();

    // 将 long 转换为 62 进制的方法
    private static String longToBase62(long num) {
        StringBuilder sb = new StringBuilder();
        if (num == 0) {
            return "0" ;
        }
        while (num > 0) {
            int index = (int) (num % 62); // 取余数，作为字符的索引
            sb.insert(0, BASE_62_CHARS.charAt(index)); // 将对应的字符添加到结果字符串的头部
            num /= 62; // 去掉已经处理过的最低位
        }
        return sb.toString();
    }

    // 将 62 进制字符串转换为 long 的方法
    private static long base62ToLong(String base62Str) {
        long result = 0;
        int len = base62Str.length();
        for (int i = 0; i < len; i++) {
            char c = base62Str.charAt(i);
            int index = BASE_62_CHARS.indexOf(c); // 找到字符在 BASE_62_CHARS 中的索引
            result = result * 62 + index; // 计算当前字符对应的数字值，并累加到结果中
        }
        return result;
    }
}
