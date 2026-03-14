package com.gwc.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * 用户登录时间表
 * </p>
 *
 * @author 购物车
 * @since 2026-02-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_login_time")
@Schema(name = "UserLoginTime对象", description = "用户登录时间表")
public class UserLoginTime implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "用户的id")
    @TableField("user_id")
    private Integer userId;

    @Schema(description = "登录时间")
    @TableField("login_time")
    private LocalDateTime loginTime;

    @Operation(description = "获得登录的日期")
    public LocalDate getLocalDate() {
        return loginTime.toLocalDate();
    }
}
