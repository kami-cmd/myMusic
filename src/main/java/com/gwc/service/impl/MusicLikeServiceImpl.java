package com.gwc.service.impl;

import com.gwc.entity.Music;
import com.gwc.entity.MusicLike;
import com.gwc.mapper.MusicLikeMapper;
import com.gwc.service.IMusicLikeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gwc.service.IMusicService;
import com.gwc.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 购物车
 * @since 2026-03-17
 */
@Service
public class MusicLikeServiceImpl extends ServiceImpl<MusicLikeMapper, MusicLike> implements IMusicLikeService {

    @Autowired
    private IMusicService musicService;

    @Override
    public boolean getIsLike(Long songId) {
        //1.拿到用户id
        Long userId = UserContext.getUserId();
        //2.通过歌曲和用户id来查
        MusicLike musicLike = lambdaQuery().eq(MusicLike::getMusicId, songId)
                .eq(MusicLike::getUserId, userId)
                .one();
        //3.有就是喜欢,没有就是不喜欢
        return musicLike != null;
    }

    @Override
    public void like(Long songId, Long isLike) {
        //1.拿到用户id
        Long userId = UserContext.getUserId();
        MusicLike musicLike = new MusicLike();
        musicLike.setMusicId(Math.toIntExact(songId)).setUserId(Math.toIntExact(userId));
        if (isLike == 1) {
            //2.喜欢.添加
            save(musicLike);
        } else {
            //3.不喜欢,删除
            lambdaUpdate().eq(MusicLike::getMusicId, songId)
                    .eq(MusicLike::getUserId, userId)
                    .remove();
        }
    }

    @Override
    public List<Music> listLike() {

        //1.拿到用户id
        Long userId = UserContext.getUserId();
        //2.通过id查询喜欢的歌的id
        List<Integer> musicIdList = lambdaQuery()
                .eq(MusicLike::getUserId, userId)
                .list().stream().map(MusicLike::getMusicId).toList();
        //3.如果是空就直接返回空集合
        if (musicIdList.isEmpty()) {
            return Collections.emptyList();
        }
        //4.把歌查出来
        List<Music> music = musicService.listByIds(musicIdList);
        //5.空了就返回空集合
        if (music == null || music.isEmpty()) {
            return Collections.emptyList();
        }
        return music;
    }
}
