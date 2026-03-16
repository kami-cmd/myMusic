package com.gwc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 歌单
 * </p>
 *
 * @author 购物车
 * @since 2026-03-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("playlist")
@Tag(name = "Playlist对象", description = "歌单")
public class Playlist implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "创建者id")
    private Integer userId;

    @Schema(description = "是否可见(0,不可见1,可见)")
    private Integer isPublic;

    @Schema
    private String coverAddress;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "歌单的简介")
    private String description;

    @Schema(description = "歌单的名字")
    private String name;

    @Schema(description = "歌单的名字")
    private Long songCount;
}
