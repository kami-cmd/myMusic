package com.gwc.service.impl;

import com.gwc.entity.UserLoginTime;
import com.gwc.entity.UserLoginTimeResult;

import com.gwc.mapper.UserLoginTimeMapper;
import com.gwc.service.IUserLoginTimeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户登录时间表 服务实现类
 * </p>
 *
 * @author 购物车
 * @since 2026-02-09
 */
@Service
public class UserLoginTimeServiceImpl extends ServiceImpl<UserLoginTimeMapper, UserLoginTime> implements IUserLoginTimeService {

    @Override
    public UserLoginTimeResult loginHistory(Long userId, Long days) {
        //1.开始时间和结束时间
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        //2.通过用户id拿到用户的登录记录
        List<UserLoginTime> userLoginTimeList = lambdaQuery().eq(UserLoginTime::getUserId, userId).list();
        //3.通过stream来分流
        Map<LocalDate, List<UserLoginTime>> listMap =
                userLoginTimeList.stream().collect(Collectors.groupingBy(UserLoginTime::getLocalDate));
        //4.基本参数
        Integer totalLoginDays = 0;
        Long totalLoginTimes = 0L;
        //5.结果对象
        UserLoginTimeResult userLogin = new UserLoginTimeResult();
        //6.传入已知信息
        userLogin.setUserId(userId);
        userLogin.setDateRange(new UserLoginTimeResult.dateRange(startDate, endDate));
        //7.为下面的循环作准备
        LocalDate today = startDate;

        List<UserLoginTimeResult.everyData> dataList = new ArrayList<>();
        //8.通过同一天来拿到当天情况,如果没有拿到就说明那天用户没有登录
        while (today.isBefore(endDate) || today.isEqual(endDate)) {
            //8.1每天都要记录
            UserLoginTimeResult.everyData everyData = new UserLoginTimeResult.everyData();
            //8.2把当天的时间传进去
            everyData.setDate(today);
            //8.3根据当天的日期,拿出把list(localDateTime)拿出来
            List<UserLoginTime> userLoginTimes = listMap.get(today);
            //8.4如果list是null或空就说明那天没有登录
            if (userLoginTimes == null || userLoginTimes.isEmpty()) {
                //8.4.1把count赋值0,给空集合后进行下次循环
                everyData.setCount(0L);
                everyData.setLoginTimes(Collections.emptyList());
                //8.4.2将每天的信息放进去
                dataList.add(everyData);
                today = today.plusDays(1);
                continue;
            }
            //8.5把list换成登录时间
            List<LocalDateTime> dateTimes = userLoginTimes.stream().map(UserLoginTime::getLoginTime).toList();
            //8.6dateTimes的size就是count
            everyData.setCount((long) dateTimes.size());
            everyData.setLoginTimes(dateTimes);
            //8.7计算总的登录次数
            totalLoginTimes += everyData.getCount();
            //8.8有登录,天数加1
            totalLoginDays++;
            //8.8将每天的信息放进去
            dataList.add(everyData);
            today = today.plusDays(1);
        }
        //9.把参数传进去
        userLogin.setDailyData(dataList);

        userLogin.setTotalLoginDays(totalLoginDays);

        userLogin.setTotalLoginTimes(totalLoginTimes);
        //9.1如果7天都没有登录那么平均登录直接设置为0
        if (totalLoginDays != 0) {
            userLogin.setAvgDailyLoginTimes(totalLoginTimes / (double) totalLoginDays);
        }
        else{
            userLogin.setAvgDailyLoginTimes(0.0);
        }
        return userLogin;
    }
}
