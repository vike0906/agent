package com.vike.agent.utils;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * @author: lsl
 * @createDate: 2019/3/15
 */
public class RandomUtil {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String[] STRS = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "v", "u", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static final String[] NUMS = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    public RandomUtil() {
    }

    public static String randomString(int length) {

        StringBuffer rs = new StringBuffer();

        for(int i = 0; i < length; ++i) {
            rs.append(STRS[RANDOM.nextInt(STRS.length)]);
        }

        return rs.toString();
    }

    public static String randomNum(int num) {

        StringBuffer rs = new StringBuffer();

        for(int i = 0; i < num; ++i) {
            rs.append(NUMS[RANDOM.nextInt(NUMS.length)]);
        }

        return rs.toString();
    }

    public static String UUID(){
        String uuid =  UUID.randomUUID().toString();
        uuid = uuid.replace("-","");
        return uuid;
    }
}
