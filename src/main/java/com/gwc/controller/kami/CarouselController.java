package com.gwc.controller.kami;


import com.gwc.entity.Carousel;
import com.gwc.entity.PageResult;
import com.gwc.entity.Result;
import com.gwc.service.ICarouselService;
import com.gwc.vo.carouselVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 购物车
 * @since 2026-03-14
 */
@RestController("kamiCaruselController")
@RequestMapping("/kami/carousel")
@Tag(name = "轮播图接口", description = "轮播图的相关操作")
public class CarouselController {
    @Autowired
    private ICarouselService carouselService;


    @GetMapping("/list")
    @Operation(description = "获取轮播图列表")
    public Result<PageResult> list(carouselVO carouselVO) {
        PageResult pageResult = carouselService.pageList(carouselVO);
        return Result.success(pageResult);
    }

    @PostMapping("/upload")
    @Operation(description = "新增轮播图")
    public Result upload(MultipartFile file, Integer musicId, Integer isUsed) throws IOException {
        carouselService.upload(file, musicId, isUsed);
        return Result.success();
    }

    @PutMapping("/update")
    @Operation(description = "修改轮播图")
    public Result update(@RequestBody Carousel carousel) {
        carouselService.update(carousel);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(description = "删除一个轮播图")
    public Result delete(@PathVariable Long id) {
        carouselService.removeById(id);
        return Result.success();
    }
}
