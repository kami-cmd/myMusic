package com.gwc.service;

import com.gwc.entity.UserLoginTime;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gwc.entity.UserLoginTimeResult;

import java.util.List;

/**
 * <p>
 * 用户登录时间表 服务类
 * </p>
 *
 * @author 购物车
 * @since 2026-02-09
 */
public interface IUserLoginTimeService extends IService<UserLoginTime> {


    UserLoginTimeResult loginHistory(Long userId, Long days);
}
