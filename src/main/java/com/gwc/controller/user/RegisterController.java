package com.gwc.controller.user;

import com.gwc.vo.RegisterVO;
import com.gwc.entity.Result;
import com.gwc.service.ForgotPasswordService;
import com.gwc.service.IUserService;
import com.gwc.utils.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.gwc.utils.StringContent.USER_PATH;

@RestController
@RequestMapping("/user/register")
@Tag(name = "注册相关操作")
public class RegisterController {
    @Autowired
    private ForgotPasswordService forgotPasswordService;
    @Autowired
    private IUserService userService;

    @PostMapping("/send-code")
    @Operation(description = "发送注册验证码")
    public Result sendCode(@RequestBody RegisterVO registerVO) {
        forgotPasswordService.sendCode(registerVO.getEmail());
        return Result.success();
    }


    @PostMapping("/uploadAvatar")
    @Operation(description = "上传头像")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        String url = FileUtils.upFile(USER_PATH, file.getBytes(), file.getOriginalFilename());
        return Result.success(url);
    }

    @PostMapping
    @Operation(description = "注册并校验验证码")
    public Result regist(@RequestBody RegisterVO registerVO){
        return userService.regist(registerVO);
    }
}
