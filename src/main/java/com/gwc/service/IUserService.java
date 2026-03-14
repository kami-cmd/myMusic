package com.gwc.service;

import com.gwc.vo.LoginVO;
import com.gwc.vo.RegisterVO;
import com.gwc.vo.UserVO;
import com.gwc.entity.PageResult;
import com.gwc.entity.Result;
import com.gwc.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gwc.entity.UserStats;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;

/**
 * <p>
 * 用户信息 服务类
 * </p>
 *
 * @author 购物车
 * @since 2026-02-08
 */
public interface IUserService extends IService<User> {

    PageResult pageList(UserVO userVO);

    UserStats fourStats();

    void updateStatus(Long id, int status);

    void conserve(UserVO userVO);

    void export(LocalDate today, HttpServletResponse response);

    Result login(LoginVO loginVO);

    Result updateByUser(UserVO userVO);

    Result regist(RegisterVO registerVO);
}
