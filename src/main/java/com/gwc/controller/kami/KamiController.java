package com.gwc.controller.kami;


import cn.hutool.core.bean.BeanUtil;
import com.gwc.vo.KamiVO;
import com.gwc.entity.Kami;
import com.gwc.entity.PageResult;
import com.gwc.entity.Result;
import com.gwc.service.IKamiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 购物车
 * @since 2026-02-12
 */
@RestController
@RequestMapping("/kami/admin")
@Tag(name = "管理员展示页面", description = "分页,添加等功能")
public class KamiController {
    @Autowired
    private IKamiService kamiService;

    @Operation(description = "分页查询")
    @GetMapping("/list")
    public Result<PageResult> pageQuery(KamiVO kamiVO) {
        PageResult pageResult = kamiService.pageQuery(kamiVO);
        return Result.success(pageResult);
    }


    @Operation(description = "查询单个管理员")
    @GetMapping("/{id}")
    public Result<Kami> one(@PathVariable Long id) {
       Kami kami= kamiService.searchById(id);
        return Result.success(kami);
    }

    @Operation(description = "删除单个管理员")
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Long id) {
        kamiService.removeById(id);
        return Result.success();
    }

    @Operation(description = "编辑管理员")
    @PutMapping
    public Result updateByKami(@RequestBody KamiVO kamiVO) {
        return  kamiService.updateByKami(kamiVO);
    }

    @Operation(description = "添加管理员")
    @PostMapping
    public Result addKami(@RequestBody Kami kami) {
        kamiService.save(kami
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now())
        );
        return Result.success();
    }

    @Operation(description = "导出管理员列表")
    @GetMapping("/export")
    public Result exportList(HttpServletResponse response) {
        kamiService.exportList(response);
        return Result.success();
    }

    @Operation(description = "获取邮箱验证码")
    @PostMapping("/send-verification")
    public Result sendVerification(@RequestParam String email ){
        kamiService.sendVerification(email);
        return Result.success();
    }
}
