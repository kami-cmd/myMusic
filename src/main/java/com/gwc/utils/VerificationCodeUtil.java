package com.gwc.utils;

import cn.hutool.core.util.RandomUtil;

public class VerificationCodeUtil {
    /**
     * 生成6位数字验证码
     */
    public static String generateCode(){
        return RandomUtil.randomNumbers(6);//生成6位数字验证码
    }
}
