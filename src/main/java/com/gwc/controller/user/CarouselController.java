package com.gwc.controller.user;


import com.gwc.entity.Carousel;
import com.gwc.entity.Result;
import com.gwc.service.ICarouselService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import static com.gwc.utils.StringContent.USED;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 购物车
 * @since 2026-03-14
 */
@RestController("userCaruselController")
@RequestMapping("/user/carousel")
@Tag(name = "user端使用轮播图", description = "有关轮播图的操作")
public class CarouselController {
    @Autowired
    private ICarouselService carouselService;

    @GetMapping("/list")
    @Operation(description = "拿到轮播图")
    public Result list() {
        return Result.success(carouselService.lambdaQuery().eq(Carousel::getIsUsed,USED).list());
    }

}
