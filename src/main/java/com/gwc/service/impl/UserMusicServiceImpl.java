package com.gwc.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.gwc.entity.Music;
import com.gwc.entity.UserMusic;
import com.gwc.mapper.UserMusicMapper;
import com.gwc.service.IUserMusicService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gwc.utils.UserContext;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 购物车
 * @since 2026-02-24
 */
@Service
public class UserMusicServiceImpl extends ServiceImpl<UserMusicMapper, UserMusic> implements IUserMusicService {

    @Override
    public List<Music> getUserMusic() {
        //拿到当前用户id
        Long userId = UserContext.getUserId();
        //通过UserMusic表拿到当前用户存储歌曲的id
        List<UserMusic> list = lambdaQuery().eq(UserMusic::getUserId, userId).list();
        //把歌曲的id转出来
        List<Integer> musicIdList = list.stream().map(UserMusic::getMusicId).toList();
        //拿到音乐的列表
        if (musicIdList.isEmpty()) {
            return Collections.emptyList();
        }
        return Db.lambdaQuery(Music.class).in(Music::getId, musicIdList).list();
    }
}
