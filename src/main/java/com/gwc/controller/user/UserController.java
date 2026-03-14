package com.gwc.controller.user;

import com.gwc.vo.UserVO;
import com.gwc.entity.Result;
import com.gwc.entity.User;
import com.gwc.service.IUserService;
import com.gwc.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user/user")
@RestController("UserUserController")
@Tag(name = "用户资料操作", description = "用户更新资料和展示资料")
public class UserController {
    @Autowired
    private IUserService userService;

    @GetMapping("/profile")
    @Operation(description = "获取当前登录用户资料")
    public Result<User> getUserById() {
        User user = userService.getById(UserContext.getUserId());
        String phone = user.getPhone();
        user.setPhone(phone.substring(0, 3) + "****" + phone.substring(7));
        return Result.success(user);
    }

    @PutMapping("/profile")
    @Operation(description = "更新用户资料")
    public Result updateById(@RequestBody UserVO userVO) {
        return userService.updateByUser(userVO);
    }


}
