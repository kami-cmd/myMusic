package com.gwc.service.impl;

import com.gwc.entity.*;
import com.gwc.service.HomeStaticService;
import com.gwc.service.IMusicService;
import com.gwc.service.IUserLoginTimeService;
import com.gwc.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.gwc.utils.StringContent.USER_LOGINDAYCOUNT;

@Service
public class HomeStaticServiceImpl implements HomeStaticService {
    private final static Integer dayAndHourNumber = 168;
    private final static Integer valueNumber = 3;
    @Autowired
    private IUserService userService;
    @Autowired
    private IMusicService musicService;
    @Autowired
    private IUserLoginTimeService userLoginTimeService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public HomeStaticResult overview() {
        //昨天登录用户数
        Long yesterdayLoginUsers = userCount(1);
        //前天登录用户数
        Long beforeYesterdayLoginUsers = userCount(2);
        //登录用户 变化比例
        Double loginUsersChange = getChange(yesterdayLoginUsers, beforeYesterdayLoginUsers);
        //总的歌曲量
        Long totalSongs = musicService.lambdaQuery().count();
        //昨天上传的歌曲数
        Long yesterdayNewSongCount = songCount(1);
        //前天上传的歌曲数
        Long beforeYesterdaySongCount = songCount(2);
        //新增歌曲变化比例
        Double newSongsChange = getChange(yesterdayNewSongCount, beforeYesterdaySongCount);
        //总的用户量
        Long totalUsers = userService.lambdaQuery().count();
        //昨天的新增用户
        Long yesterdayNewUsers = newUserCount(1);
        //前天的新增用户
        Long beforeYesterdayNewUsers = newUserCount(1);
        //新增用户变化比例
        Double newUsersChange = getChange(yesterdayNewUsers, beforeYesterdayNewUsers);
        Map<Integer, Integer> map = getBeforeDayLoginUsers(1);
        //通过遍历找到最大的人数
        Long peakHour = 0L;
        Long peakHourUsers = 0L;
        Set<Map.Entry<Integer, Integer>> entries = map.entrySet();
        for (Map.Entry<Integer, Integer> entry : entries) {
            Integer hourUsers = entry.getValue();
            if (hourUsers > peakHourUsers) {
                peakHour = Long.valueOf(entry.getKey());
                peakHourUsers = Long.valueOf(hourUsers);
            }
        }
        return new HomeStaticResult(yesterdayLoginUsers
                , loginUsersChange, yesterdayNewSongCount
                , newSongsChange
                , totalUsers
                , totalSongs
                , yesterdayNewUsers
                , newUsersChange
                , peakHour
                , peakHourUsers);
    }

    @Override
    public UserStatic userTrend() {
        UserStatic userStatic = new UserStatic();
        //1.拿到今天的时间
        LocalDate endOfDay = LocalDate.now();
        //2.拿到6天前作为起始时间
        LocalDate startOfDay = endOfDay.minusDays(6);
        //3.确定查找的起始和结束时间
        LocalDateTime end = LocalDateTime.of(endOfDay, LocalTime.MAX);
        LocalDateTime start = LocalDateTime.of(startOfDay, LocalTime.MIN);

        //3.查询在这几天新增的用户以及有登录的用户记录
        List<User> userList = userService.lambdaQuery()
                .between(User::getCreateTime, start, end).list();
        List<UserLoginTime> userLoginTimes = userLoginTimeService.lambdaQuery()
                .between(UserLoginTime::getLoginTime, start, end).list();
        //4.通过group拿到每天新增的用户以及活跃用户数量
        Map<LocalDate, List<User>> newUserList
                = userList.stream().collect(Collectors.groupingBy(User::getCreateLocalDate));
        Map<LocalDate, List<UserLoginTime>> localDateListMap
                = userLoginTimes.stream().collect(Collectors.groupingBy(UserLoginTime::getLocalDate));
        //5.查找每天活跃用户数量,通过set去重
        LocalDate temp = startOfDay;
        List<Integer> activeUser = new ArrayList<>();
        List<Integer> newUsers = new ArrayList<>();
        Set<Integer> userLoginSet = new HashSet<>();
        while (temp.isBefore(endOfDay) || temp.isEqual(endOfDay)) {
            //5.1通过找map中的list来拿到数量
            List<User> users = newUserList.get(temp);
            List<UserLoginTime> loginTimeList = localDateListMap.get(temp);
            //5.2如果list为空或null说明当天没有那种用户
            if (users == null || users.isEmpty()) {
                newUsers.add(0);
            } else {
                newUsers.add(users.size());
            }
            if (loginTimeList == null || loginTimeList.isEmpty()) {
                activeUser.add(userLoginSet.size());
            } else {
                for (UserLoginTime userLoginTime : loginTimeList) {
                    userLoginSet.add(userLoginTime.getUserId());
                }
                activeUser.add(userLoginSet.size());
            }
            temp = temp.plusDays(1);
        }

        //6.封装返回
        userStatic.setActiveUser(activeUser);
        userStatic.setNewUsers(newUsers);
        return userStatic;
    }

    @Override
    public List loginTrend() {
        //拿到昨天登录用户的时间分布
        Map<Integer, Integer> map = getBeforeDayLoginUsers(1);
        //转化成list
        List<Integer> count = new ArrayList<>();
        for (int i = 1; i <= 24; i++) {
            count.add(map.getOrDefault(i, 0));
        }
        return count;
    }

    @Override
    public SongStatic songTrend() {
        //1.今天的日期,拿到开始和结束时间
        LocalDate today = LocalDate.now();
        List<LocalDate> dates = new ArrayList<>();
        List<Long> newSongs = new ArrayList<>();
        LocalDate sevenDay = today.minusDays(6);
        LocalDateTime startOfDay = sevenDay.atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.of(today, LocalTime.MAX);
        //2.查询七天内的上传歌曲的信息
        List<Music> totalUploadSong = musicService.lambdaQuery().between(Music::getUploadTime, startOfDay, endOfDay).list();
        //3.通过stream（）进行分日期
        Map<LocalDate, List<Music>> dateListMap = totalUploadSong.stream().collect(Collectors.groupingBy(Music::getUploadDate));
        LocalDate now = sevenDay;
        while (now.isBefore(today) || now.isEqual(today)) {
            List<Music> music = dateListMap.get(now);
            //4.如果有一天是空的,就放日期后给到一个0
            dates.add(now);
            if (music == null || music.isEmpty()) {
                newSongs.add(0L);
            } else {
                newSongs.add((long) music.size());
            }
            now = now.plusDays(1);
        }
        //5.把每天的上传歌曲个数统计分类并封装返回
        return new SongStatic(dates, newSongs);
    }

    @Override
    public int[][] heatmap() {
        int[][] res = new int[dayAndHourNumber][valueNumber];
        int firstIndex = 0;
        //1.看今天是周几
        int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();
        //2.拿到现在的点数
        int hour = LocalTime.now().getHour();
        //3.拿到每天的数据
        for (int i = 0; i < dayOfWeek; i++) {
            Map<Integer, Integer> beforeDayLoginUsers = getBeforeDayLoginUsers(dayOfWeek - i - 1);
            if (i != dayOfWeek - 1) {
                //3.1之前的天数,是24小时
                for (int n = 0; n < 24; n++) {
                    Integer LoginUsersCount = beforeDayLoginUsers.getOrDefault(n, 0);
                    res[firstIndex][0] = n;
                    res[firstIndex][1] = firstIndex / 24;
                    res[firstIndex][2] = LoginUsersCount;
                    firstIndex++;
                }
            } else {
                //3.2今天,不一定是24小时
                for (int n = 0; n <= hour; n++) {
                    Integer LoginUsersCount = beforeDayLoginUsers.getOrDefault(n, 0);
                    res[firstIndex][0] = n;
                    res[firstIndex][1] = firstIndex / 24;
                    res[firstIndex][2] = LoginUsersCount;
                    firstIndex++;
                }
            }

        }
        //4.后面的日期全部用0填充
        while (firstIndex < dayAndHourNumber) {
            res[firstIndex][0] = firstIndex % 24;
            res[firstIndex][1] = firstIndex / 24;
            res[firstIndex][2] = 0;
            firstIndex++;
        }
        return res;
    }

    private static Double getChange(Long yesterdayCount, Long beforeYesterdayCount) {
        if (yesterdayCount == 0 && beforeYesterdayCount == 0) {
            return Double.parseDouble("0.00");
        }

        if (beforeYesterdayCount == 0) {
            return Double.parseDouble("100.00");
        }
        BigDecimal musicRes = BigDecimal.valueOf(yesterdayCount)
                .divide(BigDecimal.valueOf(beforeYesterdayCount), 2, RoundingMode.HALF_UP);
        //变化比例
        return musicRes.doubleValue();
    }

    private Long newUserCount(int beforDay) {
        //拿到当天的开始和结束
        LocalDate now = LocalDate.now().minusDays(beforDay);
        LocalDateTime startOfDay = now.atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.of(now, LocalTime.MAX);

        //当天新增用户数
        return userService.lambdaQuery()
                .ge(User::getCreateTime, startOfDay)
                .le(User::getCreateTime, endOfDay)
                .count();
    }

    private Long songCount(int beforDay) {
        //拿到当天的开始和结束
        LocalDate now = LocalDate.now().minusDays(beforDay);
        LocalDateTime startOfDay = now.atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.of(now, LocalTime.MAX);

        //当天上传的歌曲数
        return musicService.lambdaQuery()
                .ge(Music::getUploadTime, startOfDay)
                .le(Music::getUploadTime, endOfDay)
                .count();
    }

    private Long userCount(int beforDay) {
        //拿到当天的开始和结束
        LocalDate now = LocalDate.now().minusDays(beforDay);
        LocalDateTime startOfDay = now.atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.of(now, LocalTime.MAX);
        //当天登录的用户
        return userService.lambdaQuery()
                .ge(User::getLastLogin, startOfDay)
                .le(User::getLastLogin, endOfDay)
                .count();
    }

    private Map<Integer, Integer> getBeforeDayLoginUsers(int beforeDay) {
        //1.拿到之前的时间
        LocalDate now = LocalDate.now().minusDays(beforeDay);
        //2.统计每个小时的人数
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 1; i <= 24; i++) {
            String everyCount = stringRedisTemplate.opsForValue().get(USER_LOGINDAYCOUNT + now + ":" + i);
            if (everyCount == null || everyCount.isEmpty()) {
                map.put(i, 0);
            } else {
                map.put(i, Integer.parseInt(everyCount));
            }
        }
        return map;
    }

}
