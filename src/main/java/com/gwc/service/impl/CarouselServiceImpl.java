package com.gwc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gwc.entity.Carousel;
import com.gwc.entity.Music;
import com.gwc.entity.PageResult;
import com.gwc.mapper.CarouselMapper;
import com.gwc.service.ICarouselService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gwc.service.IMusicService;
import com.gwc.utils.FileUtils;
import com.gwc.vo.carouselVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.gwc.utils.StringContent.CAROUSEL_PATH;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 购物车
 * @since 2026-03-14
 */
@Service
public class CarouselServiceImpl extends ServiceImpl<CarouselMapper, Carousel> implements ICarouselService {

    @Autowired
    private IMusicService musicService;

    @Override
    public PageResult pageList(carouselVO carouselVO) {

        //1.创造分页器
        Page page = new Page(carouselVO.getCurrentPage(), carouselVO.getPageSize());
        //2.查询条件
        LambdaQueryWrapper<Carousel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(carouselVO.getIsUsed() != null, Carousel::getIsUsed, carouselVO.getIsUsed());
        //3.查询
        page = page(page, wrapper);
        //4.如果查询结果为空.就放空list
        List records = page.getRecords();
        if (records == null || records.isEmpty()) {
            return new PageResult(0L, Collections.emptyList());
        }
        //5.封装返回结果
        return new PageResult(page.getTotal(), records);
    }

    @Override
    public void upload(MultipartFile file, Integer musicId, Integer isUsed) throws IOException {
        //1.拿到图片进行上传后返回结果路径
        String carouselAddress = FileUtils.upFile(CAROUSEL_PATH, file.getBytes(), file.getOriginalFilename());
        //2.将关联歌曲的id放进去
        Carousel carousel = new Carousel();
        //3.查询歌曲相关信息并填充
        Music musicById = musicService.getById(musicId);
        carousel.setMusicName(musicById.getName());
        carousel.setMusicSinger(musicById.getSinger());
        carousel.setIsUsed(isUsed);
        carousel.setMusicId(musicId);
        carousel.setUploadTime(LocalDateTime.now());
        carousel.setStorageAddress(carouselAddress);
        //3.保存对象返回
        save(carousel);
    }

    @Override
    public void update(Carousel carousel) {
        //1.拿到歌曲id
        Integer musicId = carousel.getMusicId();
        //2.查找信息
        Music music = musicService.getById(musicId);
        //3.修改歌曲的name和歌手
        carousel.setMusicSinger(music.getSinger());
        carousel.setMusicName(music.getName());
        //4.基于id直接再次保存
        updateById(carousel);
        //5.结束
    }
}
