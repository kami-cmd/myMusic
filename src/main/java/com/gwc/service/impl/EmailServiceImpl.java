package com.gwc.service.impl;

import com.gwc.service.EmailService;
import com.gwc.utils.VerificationCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendCode(String email,String code) {
        //创造一个发送的对象
        SimpleMailMessage message = new SimpleMailMessage();
        //设置发送者
        message.setFrom(fromEmail);
        //发送的地方
        message.setTo(email);
        //标题
        message.setSubject("【世界的小角落】邮箱验证码");
        //内容
        message.setText("您的验证码是：" + code + "，有效期5分钟，请勿泄露。");
        //发送
        mailSender.send(message);
    }
}
