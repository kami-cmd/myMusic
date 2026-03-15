package com.gwc.service.impl;

import com.gwc.entity.Music;
import com.gwc.entity.PlaylistMusic;
import com.gwc.mapper.PlaylistMusicMapper;
import com.gwc.service.IMusicService;
import com.gwc.service.IPlaylistMusicService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 购物车
 * @since 2026-03-15
 */
@Service
public class PlaylistMusicServiceImpl extends ServiceImpl<PlaylistMusicMapper, PlaylistMusic> implements IPlaylistMusicService {
    @Autowired
    private IMusicService musicService;

    @Override
    public List<Music> getDetail(Long id) {
        //1.查询这个歌单关联的歌曲
        List<PlaylistMusic> list = lambdaQuery().eq(PlaylistMusic::getPlaylistId, id).list();
        //2.如果是空就返回空集合
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        //3.转出歌曲id
        List<Integer> musicIdList = list.stream().map(PlaylistMusic::getMusicId).toList();
        //4.根据歌曲id查歌曲
        List<Music> musicList = musicService.listByIds(musicIdList);
        if (musicList == null || musicList.isEmpty()) {
            return Collections.emptyList();
        }
        //5.返回结果
        return musicList;

    }

    @Override
    public void addSongs(Long id, List<Long> songIds) {
        //1.创造集合playlistmusic
        List<PlaylistMusic> playlistMusicList = new ArrayList<>();
        for (Long songId : songIds) {
            playlistMusicList.add(new PlaylistMusic()
                    .setMusicId(Math.toIntExact(songId))
                    .setPlaylistId(Math.toIntExact(id)));
        }
        //2.一次性发过去
        saveBatch(playlistMusicList);
    }

    @Override
    public void removeSongs(Long id, Long songId) {
        //1.根据查询条件来删除
        lambdaUpdate().eq(PlaylistMusic::getMusicId, songId)
                .eq(PlaylistMusic::getPlaylistId, id)
                .remove();
    }

    @Override
    public void deletePlaylist(Long id) {
        lambdaUpdate().eq(PlaylistMusic::getPlaylistId, id).remove();
    }


}
