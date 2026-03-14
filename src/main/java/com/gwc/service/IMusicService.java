package com.gwc.service;

import com.gwc.vo.MusicVO;
import com.gwc.entity.Music;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gwc.entity.MusicStats;
import com.gwc.entity.PageResult;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 存储的歌曲 服务类
 * </p>
 *
 * @author 购物车
 * @since 2026-02-11
 */
public interface IMusicService extends IService<Music> {

    PageResult pageSongs(MusicVO musicVO);

    MusicStats fourstatis();

    List<Music> top10();

    void exportById(Long id, HttpServletResponse response) throws IOException;

    void exportTop10(List<Integer> ids,HttpServletResponse response) throws IOException;

    void uploadMusic(MultipartFile file) throws IOException;

    String getMusicUrl(Long id);
}
