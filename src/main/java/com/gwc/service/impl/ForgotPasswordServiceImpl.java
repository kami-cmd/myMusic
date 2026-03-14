package com.gwc.service.impl;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.gwc.vo.ForgotCodeVO;
import com.gwc.vo.ResetPasswordVO;
import com.gwc.entity.Result;
import com.gwc.entity.User;
import com.gwc.service.EmailService;
import com.gwc.service.ForgotPasswordService;
import com.gwc.service.IUserService;
import com.gwc.utils.JwtUtils;
import com.gwc.utils.VerificationCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {
    @Autowired
    private EmailService emailService;
    @Autowired
    private IUserService userService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void sendCode(String email) {
        //拿到验证码
        String code = VerificationCodeUtil.generateCode();
        //发送
        emailService.sendCode(email, code);
        //存到redis里面
        redisTemplate.opsForValue().set("code:" + email, code, 5, TimeUnit.MINUTES);
    }

    @Override
    public Result verifyCode(ForgotCodeVO forgotCodeVO) {
        String account = forgotCodeVO.getAccount();
        String code = forgotCodeVO.getCode();
        String email = forgotCodeVO.getEmail();
        //保证信息没有少
        if (!StringUtils.isNotBlank(account) || !StringUtils.isNotBlank(code) || !StringUtils.isNotBlank(email)) {
            return Result.error("不能留空白");
        }
        String RedisCode = redisTemplate.opsForValue().get("code:" + email);
        if (!StringUtils.isNotBlank(RedisCode)) {
            return Result.error("请先获取验证码");
        }
        if (!code.equals(RedisCode)) {
            return Result.error("验证码有误");
        }
        //销毁验证码
        redisTemplate.delete("code:" + email);
        //拿到用户
        User one = userService.lambdaQuery().eq(User::getUserName, account)
                .eq(User::getEmail, email)
                .one();
        if (one == null) {
            return Result.error("该用户不存在");
        }
        //生成token给前端跳转到重置密码
        return Result.success(JwtUtils.generateTempToken(email));
    }

    @Override
    public Result resetPassword(ResetPasswordVO resetPasswordVO) {
        String token = resetPasswordVO.getToken();
        boolean validateToken = JwtUtils.validateToken(token);
        if(!validateToken){
            return Result.error("没有token");
        }
        if (!StringUtils.isNotBlank(resetPasswordVO.getPassword())) {
            return Result.error("密码不能为空");
        }

        String email = JwtUtils.getemail(token);
        User one = userService.lambdaQuery().eq(User::getEmail, email).one();
        one.setPassword(resetPasswordVO.getPassword());
        userService.lambdaUpdate()
                .eq(User::getId, one.getId()).update(one);
        return Result.success();
    }
}
