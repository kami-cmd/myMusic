package com.gwc.service;

import com.gwc.entity.Playlist;
import com.gwc.entity.PlaylistLike;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 购物车
 * @since 2026-03-15
 */
public interface IPlaylistLikeService extends IService<PlaylistLike> {

    List<Playlist> getLiked();

    void deletePlaylist(Long id);

    void likeOrUnlikePlaylist(Long id, Long isLike);

    boolean isLike(Long id);
}
