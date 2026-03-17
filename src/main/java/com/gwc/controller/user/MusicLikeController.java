package com.gwc.controller.user;


import com.gwc.entity.Music;
import com.gwc.entity.MusicLike;
import com.gwc.entity.Result;
import com.gwc.service.IMusicLikeService;
import com.gwc.service.IMusicService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 购物车
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/user/music-like")
public class MusicLikeController {

    @Autowired
    private IMusicLikeService musicLikeService;


    @GetMapping("/{songId}/like")
    @Operation(description = "拿到歌曲是不是喜欢")
    public Result getIsLike(@PathVariable Long songId) {
        boolean Like = musicLikeService.getIsLike(songId);
        return Result.success(Like);
    }

    @PostMapping("/{songId}/like")
    @Operation(description = "改变是否喜欢")
    public Result like(@PathVariable Long songId, @RequestParam("action") Long isLike) {
        musicLikeService.like(songId, isLike);
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(description = "获取喜欢歌单的列表")
    public Result<List<Music>> list() {
        List<Music> likeList = musicLikeService.listLike();
        return Result.success(likeList);
    }
}
