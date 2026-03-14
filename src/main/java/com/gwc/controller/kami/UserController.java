package com.gwc.controller.kami;


import com.aliyuncs.exceptions.ClientException;
import com.gwc.vo.UserVO;
import com.gwc.entity.*;
import com.gwc.service.IUserLoginTimeService;
import com.gwc.service.IUserService;
import com.gwc.utils.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

import static com.gwc.utils.StringContent.USER_PATH;

/**
 * <p>
 * 用户信息 前端控制器
 * </p>
 *
 * @author 购物车
 * @since 2026-02-08
 */
@RestController
@RequestMapping("/kami/user")
@Tag(name = "用户管理", description = "用户的创建、查询、删除等操作") // 为API分组
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IUserLoginTimeService userLoginTimeService;

    @Operation(summary = "分页查询") // 描述单个操作
    @GetMapping("/list")
    public Result<PageResult> pageList(UserVO userVO) {
        PageResult pageResult = userService.pageList(userVO);
        return Result.success(pageResult);
    }

    @Operation(summary = "统计数据")
    @GetMapping("/stats")
    public Result<UserStats> fourstats() {
        UserStats userStats = userService.fourStats();
        return Result.success(userStats);
    }

    @Operation(summary = "改变用户账号状态")
    @PutMapping("/status")
    public Result updateStatus(@RequestBody UserVO userVO) {
        userService.updateStatus(userVO.getId(), userVO.getStatus());
        return Result.success();
    }

    @Operation(summary = "添加用户")
    @PostMapping("/add")
    public Result conserve(@RequestBody UserVO userVO) {
        userService.conserve(userVO);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @PutMapping("/reset-password")
    public Result updatePassword(@RequestBody User user) {
        userService.updateById(user);
        return Result.success();
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/update")
    public Result updateUser(@RequestBody User user) {
        userService.updateById(user);
        return Result.success();
    }

    @Operation(summary = "查看用户登录记录")
    @GetMapping("/login-history")
    public Result<UserLoginTimeResult> loginHistory(@RequestParam Long userId, @RequestParam Long days) {
        UserLoginTimeResult list = userLoginTimeService.loginHistory(userId, days);
        return Result.success(list);
    }


    @Operation(summary = "导出数据")
    @GetMapping("/export")
    public Result export(@RequestParam LocalDate exportDate, HttpServletResponse response) {
        userService.export(exportDate, response);
        return Result.success();
    }

    @Operation(summary = "上传头像")
    @PostMapping("/upload-avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException, ClientException {
        String url = FileUtils.upFile(USER_PATH, file.getBytes(), file.getOriginalFilename());
        return Result.success(url);
    }

    @Operation(summary = "取消头像时的删除")
    @DeleteMapping("/avatar")
    public Result deleteAvatar(String url) {
        FileUtils.deleteFile(url,USER_PATH);
        return Result.success();
    }
}
