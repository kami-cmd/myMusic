package com.gwc.service;

import com.gwc.entity.Result;
import com.gwc.vo.KamiVO;
import com.gwc.entity.Kami;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gwc.entity.PageResult;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 购物车
 * @since 2026-02-12
 */
public interface IKamiService extends IService<Kami> {

    PageResult pageQuery(KamiVO kamiVO);

    void exportList(HttpServletResponse response);

    Kami searchById(Long id);

    void sendVerification(String email);

    Result updateByKami(KamiVO kamiVO);
}
