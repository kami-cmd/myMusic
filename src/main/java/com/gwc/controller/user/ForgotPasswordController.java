package com.gwc.controller.user;

import com.gwc.vo.ForgotCodeVO;
import com.gwc.vo.ResetPasswordVO;
import com.gwc.entity.Result;
import com.gwc.service.ForgotPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/forgot-password")
@Tag(name = "忘记密码相关操作", description = "验证码校验等")
public class ForgotPasswordController {
    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @PostMapping("/send-code")
    @Operation(description = "通过邮箱发送验证码")
    public Result sendCode(@RequestBody ForgotCodeVO forgotCodeVO) {
        forgotPasswordService.sendCode(forgotCodeVO.getEmail());
        return Result.success();
    }

    @PostMapping("/verify-code")
    @Operation(description = "验证验证码")
    public Result verifyCode(@RequestBody ForgotCodeVO forgotCodeVO) {
        return forgotPasswordService.verifyCode(forgotCodeVO);
    }

    @PostMapping("/reset")
    @Operation(description = "重置密码")
    public Result resetPassword(@RequestBody ResetPasswordVO resetPasswordVO){
        return forgotPasswordService.resetPassword(resetPasswordVO);
    }
}
