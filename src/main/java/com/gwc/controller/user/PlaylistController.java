package com.gwc.controller.user;


import com.gwc.entity.Music;
import com.gwc.entity.Playlist;
import com.gwc.entity.Result;
import com.gwc.service.IPlaylistLikeService;
import com.gwc.service.IPlaylistMusicService;
import com.gwc.service.IPlaylistService;
import com.gwc.vo.AddSongsRequest;
import com.gwc.vo.playlistVO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 歌单 前端控制器
 * </p>
 *
 * @author 购物车
 * @since 2026-03-15
 */
@RestController
@RequestMapping("/user/playlists")
public class PlaylistController {

    @Autowired
    private IPlaylistService playlistService;

    @Autowired
    private IPlaylistMusicService playlistMusicService;

    @Autowired
    private IPlaylistLikeService playlistLikeService;

    @GetMapping("/my")
    @Operation(description = "得到用户的歌单")
    public Result<List<Playlist>> my() {
        List<Playlist> playlistsList = playlistService.my();
        return Result.success(playlistsList);
    }

    @GetMapping("/liked")
    @Operation(description = "拿到喜欢的歌单")
    public Result<List<Playlist>> liked() {
        List<Playlist> playlistsList = playlistLikeService.getLiked();
        return Result.success(playlistsList);
    }

    @PostMapping("/add")
    @Operation(description = "创建歌单")
    public Result add(playlistVO playlistVO) throws IOException {
        playlistService.add(playlistVO);
        return Result.success();
    }

    @GetMapping("/{id}/songs")
    @Operation(description = "获得歌单的歌曲")
    public Result<List<Music>> getDetail(@PathVariable Long id) {
        List<Music> musicList = playlistMusicService.getDetail(id);
        return Result.success(musicList);
    }

    @GetMapping("/{id}/info")
    @Operation(description = "获取歌单信息")
    public Result<Playlist> getPlaylist(@PathVariable Long id) {
        return Result.success(playlistService.getById(id));
    }

    @PostMapping("/{id}/songs")
    @Operation(description = "添加歌单的歌曲")
    public Result addSongs(@PathVariable Long id,
                           @RequestBody AddSongsRequest addSongsRequest) {
        playlistMusicService.addSongs(id, addSongsRequest.getSongIds());
        playlistService.addSongs(id, addSongsRequest.getSongIds());
        return Result.success();
    }

    @DeleteMapping("/{id}/songs/{songId}")
    @Operation(description = "移除歌单中的歌曲")
    public Result removeSongs(@PathVariable Long id,
                              @PathVariable Long songId) {
        playlistMusicService.removeSongs(id, songId);
        playlistService.removeSongs(id);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(description = "修改歌单的信息")
    public Result updatePlaylist(@PathVariable Long id,
                                 @RequestParam("name") String name,
                                 @RequestParam(value = "description", required = false) String description,
                                 @RequestParam(value = "cover", required = false) MultipartFile cover) throws IOException {
        playlistService.updatePlaylist(id, name, description, cover);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(description = "删除歌单")
    public Result deletePlaylist(@PathVariable Long id) {
        playlistMusicService.deletePlaylist(id);
        playlistService.deletePlaylist(id);
        playlistLikeService.deletePlaylist(id);
        return Result.success();
    }

    @GetMapping("/{id}/creator")
    @Operation(description = "获得歌单创作者的名字")
    public Result getCreatorName(@PathVariable Long id) {
        return Result.success(playlistService.getCreatorName(id));
    }

    @PostMapping("/{id}/public")
    @Operation(description = "修改歌单的公开情况")
    public Result updatePublic(@PathVariable Long id, @RequestParam Long isPublic) {
        playlistService.updatePublic(id, isPublic);
        return Result.success();
    }

    @GetMapping("/public")
    @Operation(description = "获取到公开的歌单")
    public Result getPublicPlaylist() {
        List<Playlist> playlists = playlistService.getPublicPlaylist();
        return Result.success(playlists);
    }

    @PostMapping("/{id}/like")
    @Operation(description = "用户添加喜欢或取消喜欢的歌单")
    public Result likePlaylist(@PathVariable Long id,@RequestParam("action") Long isLike) {
        playlistLikeService.likeOrUnlikePlaylist(id,isLike);
        return  Result.success();
    }

    @GetMapping("/{id}/like")
    @Operation(description = "检查用户是不是喜欢这个歌单")
    public  Result isLikePlaylist(@PathVariable Long id){
        boolean isLike=playlistLikeService.isLike(id);
        return Result.success(isLike);
    }

}
