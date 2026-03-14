package com.gwc.service;

import com.gwc.vo.ForgotCodeVO;
import com.gwc.vo.ResetPasswordVO;
import com.gwc.entity.Result;

public interface ForgotPasswordService {
    void sendCode(String email);

    Result verifyCode(ForgotCodeVO forgotCodeVO);

    Result resetPassword(ResetPasswordVO resetPasswordVO);
}
