package com.gwc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gwc.vo.MusicVO;
import com.gwc.entity.Music;
import com.gwc.entity.MusicStats;
import com.gwc.entity.PageResult;
import com.gwc.entity.UserMusic;
import com.gwc.mapper.MusicMapper;
import com.gwc.service.IMusicService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gwc.service.IUserMusicService;
import com.gwc.utils.FileUtils;
import com.gwc.utils.MusicMetadataUtils;
import com.gwc.utils.UserContext;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.gwc.utils.StringContent.SONG_FILE_PATH;

/**
 * <p>
 * 存储的歌曲 服务实现类
 * </p>
 *
 * @author 购物车
 * @since 2026-02-11
 */
@Service
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements IMusicService {

    @Autowired
    private IUserMusicService userMusicService;

    @Override
    public PageResult pageSongs(MusicVO musicVO) {
        //1.创造分页条件
        Page page = new Page<>(musicVO.getCurrentPage(), musicVO.getPageSize());
        //2.1是否要进行排序
        boolean sort = StringUtils.isNotBlank(musicVO.getSortField()) && StringUtils.isNotBlank(musicVO.getSortOrder());
        if (sort) {
            //2.2确定要进行排序,对排序的字段进行处理
            String sortField = musicVO.getSortField();
            for (int i = 0; i < sortField.length(); i++) {
                char now = sortField.charAt(i);
                if (now >= 65 && now <= 90) {
                    //2.3是大写字母
                    now += 32;
                    musicVO.setSortField(sortField.substring(0, i) + "_" + now + sortField.substring(i + 1));
                    break;
                }
            }
        }
        //3.查询条件
        LambdaQueryWrapper<Music> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StringUtils.isNotBlank(musicVO.getName()), Music::getName, musicVO.getName())
                .like(StringUtils.isNotBlank(musicVO.getSinger()), Music::getSinger, musicVO.getSinger())
                .eq(musicVO.getType() != null, Music::getType, musicVO.getType())
                .last(sort,"order by "+musicVO.getSortField()+" "+musicVO.getSortOrder());
        //4.分页查询
        Page res = page(page, wrapper);

        return new PageResult(res.getTotal(), res.getRecords());
    }

    @Override
    public MusicStats fourstatis() {
        //总的歌曲
        Long totalSing = lambdaQuery().count();
        List<Music> list = lambdaQuery().list();
        //储存空间
        List<String> sizes = list.stream().map(Music::getSize).toList();
        Double totalSize = 0.0;
        for (String s : sizes) {
            String size = s.substring(0, s.indexOf("MB"));
            totalSize += Double.valueOf(size);
        }
        //播放量
        List<Long> plays = list.stream().map(Music::getPlayCount).toList();
        Long totalPlays = 0L;
        for (Long evey : plays) {
            totalPlays += evey;
        }
        //今天的日期
        LocalDate now = LocalDate.now();
        LocalDateTime startOfDay = now.atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.of(now, LocalTime.MAX);

        //今日新增
        Long todayNew = lambdaQuery().ge(Music::getUploadTime, startOfDay)
                .le(Music::getUploadTime, endOfDay).count();

        DecimalFormat df = new DecimalFormat("#0.00");
        String resTotalSize = df.format(totalSize) + "MB";
        return new MusicStats(totalSing, resTotalSize, todayNew, totalPlays);
    }

    @Override
    public List<Music> top10() {
        return lambdaQuery().orderByDesc(Music::getPlayCount).last("LIMIT 10").list();
    }

    @Override
    public void exportById(Long id, HttpServletResponse response) throws IOException {
        //拿到存储的文件名字
        String fileName = lambdaQuery().eq(Music::getId, id).one().getStorageAddress();
        //下载
        FileUtils.downloadFile(SONG_FILE_PATH, response, fileName);
    }

    @Override
    public void exportTop10(List<Integer> ids, HttpServletResponse response) throws IOException {
        List<Music> top10List = lambdaQuery().in(Music::getId, ids).list();
        List<String> top10AddressList = top10List.stream().map(Music::getStorageAddress).toList();
        FileUtils.downloadFilesByZip(SONG_FILE_PATH, response, top10AddressList);
    }

    @Override
    public void uploadMusic(MultipartFile file) throws IOException {
        //拿到各项数据
        Music music = MusicMetadataUtils.parse(file);
        Music one = lambdaQuery().eq(StringUtils.isNotBlank(music.getName()), Music::getName, music.getName())
                .eq(StringUtils.isNotBlank(music.getSinger()), Music::getSinger, music.getSinger())
                .one();
        //当前用户id
        Long userId = UserContext.getUserId();
        if (one != null) {
            UserMusic userMusic = new UserMusic();
            userMusic.setUserId(Math.toIntExact(userId));
            userMusic.setMusicId(one.getId());
            userMusicService.save(userMusic);
            return;
        }
        music.setPlayCount(0L);
        //上传时间
        music.setUploadTime(LocalDateTime.now());
        //拿到存储歌曲的文件名
        String upPath = FileUtils.upFile(SONG_FILE_PATH, file.getBytes(), file.getOriginalFilename());
        music.setStorageAddress(upPath.substring(upPath.lastIndexOf("/")+1));
        //存到数据库
        save(music);
        //存下表
        UserMusic userMusic = new UserMusic();
        userMusic.setUserId(Math.toIntExact(userId));
        userMusic.setMusicId(music.getId());
        userMusicService.save(userMusic);
    }

    @Override
    public String getMusicUrl(Long id) {
        Music one = lambdaQuery().eq(Music::getId, id).one();
        String storageAddress = one.getStorageAddress();
        Long playCount = one.getPlayCount();
        one.setPlayCount(++playCount);
        lambdaUpdate().eq(Music::getId,one.getId())
                .update(one);
        return "http:" + "//" + "gwc-music.oss-cn-beijing.aliyuncs.com" + "/" + SONG_FILE_PATH +"/"+  storageAddress;
    }
}
