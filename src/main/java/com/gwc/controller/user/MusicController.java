package com.gwc.controller.user;

import com.gwc.vo.MusicVO;
import com.gwc.entity.Music;
import com.gwc.entity.PageResult;
import com.gwc.entity.Result;
import com.gwc.service.IMusicService;
import com.gwc.service.IUserMusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController("UserMusicController")
@RequestMapping("/user/music")
@Tag(name = "用户有关音乐的操作", description = "用户音乐页面的请求")
public class MusicController {
    @Autowired
    private IMusicService musicService;
    @Autowired
    private IUserMusicService userMusicService;

    @PostMapping("/upload")
    @Operation(description = "上传音乐文件")
    public Result uploadMusic(@RequestParam("file") MultipartFile file) throws IOException {
        musicService.uploadMusic(file);
        return Result.success();
    }

    @GetMapping("/top-songs")
    @Operation(description = "用户得到top10歌曲")
    public Result<List<Music>> topSongs() {
        return Result.success(musicService.top10());
    }

    @GetMapping("/music-list")
    @Operation(description = "查看歌曲")
    public Result<PageResult> querrySongs(MusicVO musicVO) {
        return Result.success(musicService.pageSongs(musicVO));
    }

    @GetMapping("/song/{songId}/play-url")
    @Operation(description = "播放音乐")
    public Result<String> playMusic(@PathVariable Long songId) {
        String url = musicService.getMusicUrl(songId);
        return Result.success(url);
    }

    @GetMapping("/song/{songId}/download")
    @Operation(description = "下载歌曲")
    public void download(@PathVariable Long songId, HttpServletResponse servletResponse) throws IOException {
        musicService.exportById(songId, servletResponse);
    }

    @GetMapping("/my-music")
    @Operation(description = "获取用户的音乐列表")
    public Result getUserMusic(){
        List<Music> list=userMusicService.getUserMusic();
        return Result.success(list);
    }
}
