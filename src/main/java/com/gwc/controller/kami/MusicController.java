package com.gwc.controller.kami;


import com.gwc.vo.MusicVO;
import com.gwc.entity.Music;
import com.gwc.entity.MusicStats;
import com.gwc.entity.PageResult;
import com.gwc.entity.Result;
import com.gwc.service.IMusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 存储的歌曲 前端控制器
 * </p>
 *
 * @author 购物车
 * @since 2026-02-11
 */
@RestController("KamiMusicController")
@RequestMapping("/kami/music")
@Tag(name = "存储音乐管理" ,description = "展示已经传的音乐,导出等操作")
public class MusicController {
    @Autowired
    private IMusicService musicService;

    @Operation(description = "分页查询")
    @GetMapping("/songs")
    public Result<PageResult> songs(MusicVO musicVO) {
        PageResult pageResult = musicService.pageSongs(musicVO);
        return Result.success(pageResult);
    }

    @Operation(description = "统计数据")
    @GetMapping("/statistics")
    public Result<MusicStats> fourstatis() {
        MusicStats musicStats = musicService.fourstatis();
        return Result.success(musicStats);
    }

    @Operation(description = "播放量前10歌曲展示")
    @GetMapping("/top10")
    public Result<List> top10() {
        List<Music> top10 = musicService.top10();
        return Result.success(top10);
    }

    @Operation(description = "根据id删歌曲")
    @DeleteMapping("/songs/{id}")
    public Result deleteById(@PathVariable Long id) {
        musicService.removeById(id);
        return Result.success();
    }

    @Operation(description = "根据id导出音乐")
    @GetMapping("/export/{id}")
    public Result exportById(@PathVariable Long id, HttpServletResponse response) throws IOException {
        musicService.exportById(id,response);
        return Result.success();
    }

    @Operation(description = "导出top10的歌曲")
    @GetMapping("/export-top10")
    public Result exportTop10(@RequestParam List<Integer> ids,HttpServletResponse response) throws IOException {
        musicService.exportTop10(ids,response);
        return Result.success();
    }
}
