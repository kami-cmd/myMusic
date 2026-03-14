package com.gwc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gwc.vo.LoginVO;
import com.gwc.vo.RegisterVO;
import com.gwc.vo.UserVO;
import com.gwc.entity.*;
import com.gwc.enums.UserLevel;
import com.gwc.enums.UserStatus;
import com.gwc.mapper.UserMapper;
import com.gwc.service.IUserLoginTimeService;
import com.gwc.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gwc.utils.JwtUtils;
import com.gwc.utils.UserContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.gwc.enums.UserStatus.FREEZE;
import static com.gwc.utils.StringContent.USER_LOGINDAYCOUNT;
import static com.gwc.utils.StringContent.USER_LOGINSTATUS;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author 购物车
 * @since 2026-02-08
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private IUserLoginTimeService userLoginTimeService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //单线程
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public PageResult pageList(UserVO userVO) {
        //1.给分页设置参数
        Page page = new Page(userVO.getCurrentPage(), userVO.getPageSize());
        //2.创造查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        //3.是否排序
        boolean sort = StringUtils.isNotBlank(userVO.getSortField()) && StringUtils.isNotBlank(userVO.getSortOrder());
        if (sort) {
            //2.2确定要进行排序,对排序的字段进行处理
            String sortField = userVO.getSortField();
            for (int i = 0; i < sortField.length(); i++) {
                char now = sortField.charAt(i);
                if (now >= 65 && now <= 90) {
                    //2.3是大写字母
                    now += 32;
                    userVO.setSortField(sortField.substring(0, i) + "_" + now + sortField.substring(i + 1));
                    break;
                }
            }
        }
        //4.精准查询
        wrapper.eq(userVO.getStatus() != null, User::getStatus, userVO.getStatus())
                .eq(userVO.getLevel() != null, User::getLevel, userVO.getLevel());

        //5.模糊查询
        wrapper.like(StrUtil.isNotBlank(userVO.getEmail()), User::getEmail, userVO.getEmail())
                .like(StrUtil.isNotBlank(userVO.getPhone()), User::getPhone, userVO.getPhone())
                .like(StrUtil.isNotBlank(userVO.getNickName()), User::getNickName, userVO.getNickName())
                .like(StrUtil.isNotBlank(userVO.getUserName()), User::getUserName, userVO);

        //6.日期
        wrapper.le(userVO.getCreateTimeEnd() != null, User::getCreateTime, userVO.getCreateTimeEnd())
                .ge(userVO.getCreateTimeStart() != null, User::getCreateTime, userVO.getCreateTimeStart());
        //7.排序
        wrapper.last(sort,"order by "+userVO.getSortField()+" "+userVO.getSortOrder());
        Page res = page(page, wrapper);

        return new PageResult(res.getTotal(), res.getRecords());
    }

    @Override
    public UserStats fourStats() {
        //总的用户量
        Long totalUsers = lambdaQuery().count();
        //今天开局
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        Long newUsersToday = lambdaQuery().ge(User::getCreateTime, todayStart).count();
        //活跃的用户量(近7天)
        LocalDate sevenDay = today.minusDays(7);
        LocalDateTime sevenDayStart = sevenDay.atStartOfDay();
        Long activeUsers = lambdaQuery().ge(User::getLastLogin, sevenDayStart).count();
        //vip
        Long vipUsers = lambdaQuery().in(User::getLevel, UserLevel.Vip.getCode(), UserLevel.Svip.getCode()).count();
        //赋值,返回
        UserStats userStats = UserStats.builder()
                .activeUsers(activeUsers)
                .newUsersToday(newUsersToday)
                .totalUsers(totalUsers)
                .vipUsers(vipUsers)
                .build();
        return userStats;
    }

    @Override
    public void updateStatus(Long id, int status) {
        User user = User.builder().status(status).build();
        lambdaUpdate().eq(User::getId, id).update(user);
    }

    @Override
    public void conserve(UserVO userVO) {
        User user = BeanUtil.copyProperties(userVO, User.class);
        LocalDateTime now = LocalDateTime.now();
        //补全参数
        user.setLastLogin(now);
        user.setCreateTime(now);
        //默认登录了一次
        user.setLoginCount(1);
        //保存
        save(user);
    }

    @Override
    public void export(LocalDate today, HttpServletResponse response) {
        //拿到今天的数据
        UserStats userStats = this.fourStats();

        try {
            //写到excel表格中
            //引入模板excel
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("templates/model.xlsx");
            //创建工作簿
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            //拿到表
            XSSFSheet sheet1 = workbook.getSheet("Sheet1");
            XSSFRow row = sheet1.getRow(0);
            //拿到待补充的"当前日期:"
            String tempDay = row.getCell(0).getStringCellValue();
            //补充完整
            String res = tempDay + today.toString();
            //写入
            row.getCell(0).setCellValue(res);
            //再将其余的数据写入
            row = sheet1.getRow(2);
            for (int i = 0; i <= 8; i += 2) {
                //拿到每个位置
                XSSFCell cell = row.getCell(i);
                if (i == 0) {
                    cell.setCellValue(userStats.getTotalUsers());
                } else if (i == 2) {
                    cell.setCellValue(userStats.getActiveUsers());
                } else if (i == 4) {
                    cell.setCellValue(userStats.getNewUsersToday());
                } else if (i == 6) {
                    cell.setCellValue(userStats.getVipUsers());
                } else if (i == 8) {
                    cell.setCellValue((double) userStats.getVipUsers() / userStats.getTotalUsers());
                }
            }
            //七天前
            LocalDate now = today.minusDays(6);
            int count = 0;
            while (now.isEqual(today) || now.isBefore(today)) {
                //拿到这一行
                row = sheet1.getRow(4 + count);
                //在第一个位置写下今天的日期
                row.getCell(0).setCellValue(now);
                //拿到当天的开始和结束
                LocalDateTime startOfDay = now.atStartOfDay();
                LocalDateTime endOfDay = LocalDateTime.of(now, LocalTime.MAX);

                //今天登录数
                Long todayLoginCount = lambdaQuery().ge(User::getLastLogin, startOfDay)
                        .le(User::getLastLogin, endOfDay)
                        .count();

                //今天新增加数
                Long todayAddCount = lambdaQuery().ge(User::getCreateTime, startOfDay)
                        .le(User::getCreateTime, endOfDay)
                        .count();

                //写下查询到的数据
                row.getCell(4).setCellValue(todayLoginCount);
                row.getCell(8).setCellValue(todayAddCount);
                now = now.plusDays(1);
                count++;
            }
            //把文件通过流给到浏览器
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);
            //关流
            out.flush(); // 确保数据完全写出
            out.close();
            workbook.close();
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Result login(LoginVO loginVO) {
        //1.校验信息
        User one = lambdaQuery()
                .eq(loginVO.getAccount() != null, User::getUserName, loginVO.getAccount())
                .eq(loginVO.getPassword() != null, User::getPassword, loginVO.getPassword())
                .one();
        if (one == null) {
            //1.1没找到
            return Result.error("账号或密码有误");
        }
        if (one.getStatus().equals(FREEZE.getCode())) {
            return Result.error("用户已冻结");
        }
        //2.查看有没有人已经登录了
        String isLogin = stringRedisTemplate.opsForValue().get("user:loginStatus:" + one.getId());
        if (StrUtil.isNotBlank(isLogin)) {
            return Result.error("已经有用户登录了");
        }
        //2.1通过redis实现锁
        stringRedisTemplate.opsForValue().set(USER_LOGINSTATUS+ one.getId(), "1",2,TimeUnit.HOURS);
        //3.在redis里面记录
        Boolean isSuccess = stringRedisTemplate.opsForValue().setIfAbsent(USER_LOGINDAYCOUNT + LocalDate.now()
                        + ":"+LocalTime.now().getHour()
                , "1", 7, TimeUnit.DAYS);
        if (!Boolean.TRUE.equals(isSuccess)) {
            stringRedisTemplate.opsForValue().increment(USER_LOGINDAYCOUNT +
                    LocalDate.now()+":"+LocalTime.now().getHour(), 1);
        }
        //4.开启异步线程处理sql储存
        executorService.submit(() -> saveUserLoginTimeAndCount(one));
        //5.登录成功后下发token
        String token = JwtUtils.generateToken(Long.valueOf(one.getId()));
        return Result.success(token);
    }

    private void saveUserLoginTimeAndCount(User one) {
        //1.登录时间的sql记录
        LocalDateTime now = LocalDateTime.now();
        lambdaUpdate().set(User::getLastLogin, now)
                .set(User::getLoginCount, one.getLoginCount() + 1)
                .eq(User::getId, one.getId())
                .update();
        //2.保存信息
        UserLoginTime userLoginTime = new UserLoginTime();
        userLoginTime.setUserId(one.getId());
        userLoginTime.setLoginTime(now);
        //3.存入
        userLoginTimeService.save(userLoginTime);
    }

    @Override
    public Result updateByUser(UserVO userVO) {
        //拿到当前id
        Long userId = UserContext.getUserId();
        User user = BeanUtil.copyProperties(userVO, User.class);
        user.setId(Math.toIntExact(userId));
        if (StringUtils.isNotBlank(userVO.getPassword()) && StringUtils.isNotBlank(userVO.getOldPassword())) {
            updateById(user);
            return Result.success();
        }
        if (userVO.getPassword() == null || userVO.getPassword().isEmpty()) {
            return Result.error("密码不能为空");
        }
        if (userVO.getOldPassword() == null || userVO.getOldPassword().isEmpty()) {
            return Result.error("旧密码不能为空");
        }
        User one = lambdaQuery().eq(User::getId, userId).one();
        if (!one.getPassword().equals(user.getPassword())) {
            return Result.error("旧密码不正确");
        }
        updateById(user);
        return Result.success();
    }

    @Override
    public Result regist(RegisterVO registerVO) {
        String redisCode = String.valueOf(stringRedisTemplate.opsForValue().get("code:" + registerVO.getEmail()));
        if (!StringUtils.isNotBlank(redisCode)) {
            return Result.error("验证码未生效");
        }
        User user = BeanUtil.copyProperties(registerVO, User.class);
        user.setLevel(UserLevel.Normal.getCode());
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setLastLogin(now);
        user.setLoginCount(1);
        user.setStatus(UserStatus.NORMAL.getCode());
        save(user);
        String token = JwtUtils.generateToken(Long.valueOf(user.getId()));
        return Result.success(token);
    }
}
