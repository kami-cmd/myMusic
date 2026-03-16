package com.gwc.service;

import com.gwc.entity.Playlist;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gwc.vo.playlistVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 歌单 服务类
 * </p>
 *
 * @author 购物车
 * @since 2026-03-15
 */
public interface IPlaylistService extends IService<Playlist> {


    List<Playlist> my();

    void add(playlistVO playlistVO) throws IOException;

    void updatePlaylist(Long id, String name, String description, MultipartFile cover) throws IOException;

    void deletePlaylist(Long id);

    String getCreatorName(Long id);

    void updatePublic(Long id, Long isPublic);

    void removeSongs(Long id);

    void addSongs(Long id, List<Long> songIds);
}
