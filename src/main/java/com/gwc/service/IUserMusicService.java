package com.gwc.service;

import com.gwc.entity.Music;
import com.gwc.entity.UserMusic;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 购物车
 * @since 2026-02-24
 */
public interface IUserMusicService extends IService<UserMusic> {

    List<Music> getUserMusic();
}
