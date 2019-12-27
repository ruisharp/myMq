package com.my.mq.commom.demo.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机数生成工具
 *
 * @author xuchaoguo
 */
public final class RandomUtil {
    /**
     * 随机数静态对象
     */
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    /**
     * 所有常见字符，包括字母大小写、数字
     */
    private static final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /**
     * 所有字母，包括大小写
     */
    private static final String LETTERCHAR = "abcdefghijkllmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /**
     * 所有数字
     */
    private static final String NUMBERCHAR = "0123456789";

    /**
     * 返回一个定长的随机字符串(只包含大小写字母、数字)
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String generateString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(RANDOM.nextInt(ALLCHAR.length())));
        }
        return sb.toString();
    }

    /**
     * 返回一个定长的数字字符串
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String generateNumString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(NUMBERCHAR.charAt(RANDOM.nextInt(NUMBERCHAR.length())));
        }
        return sb.toString();
    }

    /**
     * 返回一个定长的随机纯字母字符串(只包含大小写字母)
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String generateMixString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(RANDOM.nextInt(LETTERCHAR.length())));
        }
        return sb.toString();
    }

    /**
     * 返回一个定长的随机纯大写字母字符串(只包含大小写字母)
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String generateLowerString(int length) {
        return generateMixString(length).toLowerCase();
    }

    /**
     * 返回一个定长的随机纯小写字母字符串(只包含大小写字母)
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String generateUpperString(int length) {
        return generateMixString(length).toUpperCase();
    }

    /**
     * 生成一个定长的纯0字符串
     *
     * @param length 字符串长度
     * @return 纯0字符串
     */
    public static String generateZeroString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append('0');
        }
        return sb.toString();
    }

    /**
     * 根据数字生成一个定长的字符串，长度不够前面补0
     *
     * @param num    数字
     * @param length 字符串长度
     * @return 定长的字符串
     */
    public static String toFixedLengthString(long num, int length) {
        StringBuilder sb = new StringBuilder();
        String strNum = String.valueOf(num);
        if (length - strNum.length() >= 0) {
            sb.append(generateZeroString(length - strNum.length()));
        } else {
            throw new RuntimeException("将数字" + num + "转化为长度为" + length
                    + "的字符串发生异常！");
        }
        sb.append(strNum);
        return sb.toString();
    }
}
