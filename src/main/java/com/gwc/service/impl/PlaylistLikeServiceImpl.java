package com.gwc.service.impl;

import com.gwc.entity.Playlist;
import com.gwc.entity.PlaylistLike;
import com.gwc.mapper.PlaylistLikeMapper;
import com.gwc.service.IPlaylistLikeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gwc.service.IPlaylistService;
import com.gwc.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.gwc.utils.StringContent.PUBLIC;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 购物车
 * @since 2026-03-15
 */
@Service
public class PlaylistLikeServiceImpl extends ServiceImpl<PlaylistLikeMapper, PlaylistLike> implements IPlaylistLikeService {

    @Autowired
    private IPlaylistService playlistService;

    @Override
    public List<Playlist> getLiked() {
        //1.拿到当前用户id
        Long userId = UserContext.getUserId();
        //2.根据id查询歌单,如果是空就返回空集合
        List<PlaylistLike> playlistLikes = lambdaQuery().eq(userId != null, PlaylistLike::getUserId, userId).list();
        if (playlistLikes == null || playlistLikes.isEmpty()) {
            return Collections.emptyList();
        }
        //3.拿到歌单的id
        List<Integer> playlistsId = playlistLikes.stream().map(PlaylistLike::getPlaylistId).toList();
        //4.根据歌单id查出来
        return playlistService.lambdaQuery().eq(Playlist::getIsPublic,PUBLIC).in(Playlist::getId,playlistsId).list();
    }

    @Override
    public void deletePlaylist(Long id) {
        lambdaUpdate().eq(PlaylistLike::getPlaylistId, id).remove();
    }

    @Override
    public void likeOrUnlikePlaylist(Long id, Long isLike) {
        //1.拿到当前用户的id
        Long userId = UserContext.getUserId();
        //2.如果是喜欢就增加
        if (isLike == 1) {
            PlaylistLike playlistLike = new PlaylistLike().setPlaylistId(Math.toIntExact(id)).setUserId(Math.toIntExact(userId));
            save(playlistLike);
        }
        //3.如果是不喜欢就删除
        else {
            lambdaUpdate().eq(PlaylistLike::getUserId, userId)
                    .eq(PlaylistLike::getPlaylistId, id)
                    .remove();
        }
    }

    @Override
    public boolean isLike(Long id) {
        //1.拿到用户id
        Long userId = UserContext.getUserId();
        //2.进行查找是否有符合的俩个
        PlaylistLike one = lambdaQuery().eq(PlaylistLike::getPlaylistId, id)
                .eq(PlaylistLike::getUserId, userId)
                .one();
        //3.有就是喜欢,没有就是不喜欢
        return one != null;
    }
}
