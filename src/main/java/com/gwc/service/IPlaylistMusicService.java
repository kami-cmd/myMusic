package com.gwc.service;

import com.gwc.entity.Music;
import com.gwc.entity.PlaylistMusic;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 购物车
 * @since 2026-03-15
 */
public interface IPlaylistMusicService extends IService<PlaylistMusic> {

    List<Music> getDetail(Long id);

    void addSongs(Long id, List<Long> songIds);

    void removeSongs(Long id, Long songId);


    void deletePlaylist(Long id);
}
