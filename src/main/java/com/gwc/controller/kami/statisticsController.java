package com.gwc.controller.kami;


import com.gwc.entity.HomeStaticResult;
import com.gwc.entity.Result;
import com.gwc.entity.SongStatic;
import com.gwc.entity.UserStatic;
import com.gwc.service.HomeStaticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/kami/statistics")
@Tag(name = "后端查看数据", description = "展示一些歌曲和用户的数据")
public class statisticsController {

    @Autowired
    private HomeStaticService staticService;

    @Operation(description = "顶部卡片的数据获取")
    @GetMapping("/overview")
    public Result<HomeStaticResult> overview() {
        HomeStaticResult homeStaticResult = staticService.overview();
        return Result.success(homeStaticResult);
    }

    @Operation(description = "近7日新增用户和登录趋势")
    @GetMapping("/user-trend")
    public Result<UserStatic> userTrend() {
        UserStatic userStatic = staticService.userTrend();
        return Result.success(userStatic);
    }

    @Operation(description = "昨日登录时间分布")
    @GetMapping("/login-trend")
    public Result<List> loginTrend() {
        List count = staticService.loginTrend();
        return Result.success(count);
    }

    @Operation(description = "近7天歌曲上传趋势")
    @GetMapping("/song-trend")
    public Result<SongStatic> songTrend(){
       SongStatic songStatic= staticService.songTrend();
        return Result.success(songStatic);
    }

    @Operation(description = "用户活跃热力图")
    @GetMapping("/heatmap")
    public Result heatmap(){
     int[][] res= staticService.heatmap();
        return Result.success(res);
    }

}
