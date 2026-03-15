package com.gwc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.gwc.entity.Playlist;
import com.gwc.entity.User;
import com.gwc.mapper.PlaylistMapper;
import com.gwc.service.IPlaylistService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gwc.service.IUserService;
import com.gwc.utils.FileUtils;
import com.gwc.utils.UserContext;
import com.gwc.vo.playlistVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.gwc.utils.StringContent.PLAYLIST_COVER_PATH;
import static com.gwc.utils.StringContent.PRIVATE;

/**
 * <p>
 * 歌单 服务实现类
 * </p>
 *
 * @author 购物车
 * @since 2026-03-15
 */
@Service
public class PlaylistServiceImpl extends ServiceImpl<PlaylistMapper, Playlist> implements IPlaylistService {

    @Autowired
    private IUserService userService;

    @Override
    public List<Playlist> my() {
        //1.拿到用户id
        Long userId = UserContext.getUserId();
        //2.查询和用户id一样的歌单并返回
        List<Playlist> playlistList = lambdaQuery().eq(userId != null, Playlist::getUserId, userId).list();
        //3.如果为空就返回一个空集合
        if (playlistList == null || playlistList.isEmpty()) {
            return Collections.emptyList();
        }
        return playlistList;
    }

    @Override
    public void add(playlistVO playlistVO) throws IOException {
        //1.拿到当前用户id
        Long userId = UserContext.getUserId();
        //2.上传图片
        MultipartFile file = playlistVO.getCover();
        String coverAddress
                = FileUtils.upFile(PLAYLIST_COVER_PATH, file.getBytes(), file.getOriginalFilename());
        //3.new一个playlist对象拿到封面地址和描述和名字
        Playlist playlist = BeanUtil.copyProperties(playlistVO, Playlist.class);
        playlist.setCoverAddress(coverAddress);
        //4.设置基本时间和用户id
        LocalDateTime now = LocalDateTime.now();
        playlist.setIsPublic(PRIVATE);
        playlist.setCreateTime(now).setUpdateTime(now);
        playlist.setUserId(Math.toIntExact(userId));
        //5.保存
        save(playlist);
    }

    @Override
    public void updatePlaylist(Long id, String name, String description, MultipartFile cover) throws IOException {
        //1.如果封面不为空说明要改封面
        Playlist playlist = new Playlist();
        if (cover != null) {
            playlist = getById(id);
            //2.如果改封面就把当前的封面删除
            //2.1拿到歌单对象拿到里面的封面进行删除
            FileUtils.deleteFile(playlist.getCoverAddress(), PLAYLIST_COVER_PATH);
            //3.上传封面
            String coverAddress = FileUtils.upFile(PLAYLIST_COVER_PATH, cover.getBytes(), cover.getOriginalFilename());
            playlist.setCoverAddress(coverAddress);
        }
        //4.创造歌单对象进行填充
        if (name != null) {
            playlist.setName(name);
        }
        if (description != null) {
            playlist.setDescription(description);
        }
        playlist.setId(Math.toIntExact(id));
        //5.用整个对象修改
        updateById(playlist);

    }

    @Override
    public void deletePlaylist(Long id) {
        //1.先拿到对象,取出封面位置
        Playlist playlist = getById(id);
        String coverAddress = playlist.getCoverAddress();
        //2.删除封面
        FileUtils.deleteFile(coverAddress, PLAYLIST_COVER_PATH);
        //3.再删除歌单
        removeById(id);
    }

    @Override
    public String getCreatorName(Long id) {
        //1.查找现在的歌单信息
        Playlist playlist = getById(id);
        //2.拿到userid
        Integer userId = playlist.getUserId();
        //3.查找user的名字
        User user = userService.getById(userId);
        //4.返回
        return user.getUserName();
    }

    @Override
    public void updatePublic(Long id, Long isPublic) {
        lambdaUpdate().eq(Playlist::getId, id)
                .set(Playlist::getIsPublic, isPublic)
                .update();
    }


}
