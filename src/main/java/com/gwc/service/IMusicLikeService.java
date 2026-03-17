package com.gwc.service;

import com.gwc.entity.Music;
import com.gwc.entity.MusicLike;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 购物车
 * @since 2026-03-17
 */
public interface IMusicLikeService extends IService<MusicLike> {

    boolean getIsLike(Long songId);

    void like(Long songId, Long isLike);

    List<Music> listLike();
}
