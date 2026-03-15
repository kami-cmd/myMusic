package com.gwc.service;

import com.gwc.entity.Carousel;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gwc.entity.PageResult;
import com.gwc.vo.carouselVO;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 购物车
 * @since 2026-03-14
 */
public interface ICarouselService extends IService<Carousel> {

    PageResult pageList(carouselVO carouselVO);

    void upload(MultipartFile file, Integer musicId, Integer isUsed) throws IOException;

    void update(Carousel carousel);
}
