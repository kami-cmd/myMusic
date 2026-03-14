package com.gwc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 存储的歌曲
 * </p>
 *
 * @author 购物车
 * @since 2026-02-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("music")
@Schema(name = "Music对象", description = "存储的歌曲")
public class Music implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "歌名")
    private String name;

    @Schema(description = "歌手")
    private String singer;

    @Schema(description = "时长")
    private String time;

    @Schema(description = "文件大小")
    private String size;

    @Schema(description = "歌曲类型,0.未知,1.摇滚,2.流行")
    private Integer type;

    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;

    @Schema(description = "播放量")
    private Long playCount;

    @Schema(description = "储存地址")
    private String storageAddress;

    @Schema
    private String coverAddress;

    @Schema(description = "专辑")
    private String album;

    @Operation(description = "拿到上传日期")
    public LocalDate getUploadDate(){
        return uploadTime.toLocalDate();
    }
}
