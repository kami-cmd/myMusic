package com.gwc.controller.kami;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.gwc.vo.LoginVO;
import com.gwc.entity.Kami;
import com.gwc.entity.Result;
import com.gwc.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/kami/login")
@RestController
@Tag(name = "登录接口", description = "校验登录")
public class LoginController {
    @PostMapping
    @Operation(description = "登录接口")
    public Result login(@RequestBody LoginVO loginVO) {
        Kami one = Db.lambdaQuery(Kami.class).eq(Kami::getAccount, loginVO.getAccount())
                .eq(Kami::getPassword, loginVO.getPassword()).one();
        if (one != null) {
            String token = JwtUtils.generateToken(Long.valueOf(one.getId()));
            return Result.success(token);
        } else {
            return Result.error("账号或密码错误");
        }
    }
}
