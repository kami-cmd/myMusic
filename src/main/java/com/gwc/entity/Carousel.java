package com.gwc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 购物车
 * @since 2026-03-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("carousel")
@Schema(name="Carousel对象", description="轮播图")
public class Carousel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "是否使用,1使用0停用")
    private Integer isUsed;

    @Schema(description = "轮播图片的上传时间")
    private LocalDateTime uploadTime;

    @Schema(description = "储存的地址(从http开始)")
    private String storageAddress;

    @Schema(description = "关联的歌曲id,用来播放歌曲")
    private Integer musicId;

    @Schema(description = "关联的歌曲名字,用来播放歌曲")
    private String musicName;

    @Schema(description = "关联的歌曲歌手,用来播放歌曲")
    private String musicSinger;

}
