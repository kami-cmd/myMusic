package com.gwc.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

// 替换这里！！！从旧版改为新版
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
// 删除旧的导入
// import io.swagger.annotations.ApiModel;
// import io.swagger.annotations.ApiModelProperty;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 用户信息
 * </p>
 *
 * @author 购物车
 * @since 2026-02-08
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
@Schema(name = "User对象", description = "用户信息")  // 替换 @ApiModel
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")  // 替换 @ApiModelProperty
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "账号")
    @TableField("user_name")
    private String userName;

    @Schema(description = "用户名称")
    @TableField("nick_name")
    private String nickName;

    @Schema(description = "手机号")
    @TableField("phone")
    private String phone;

    @Schema(description = "邮箱")
    @TableField("email")
    private String email;

    @Schema(description = "头像")
    @TableField("avatar")
    private String avatar;

    @Schema(description = "性别")
    @TableField("gender")
    private Integer gender;

    @Schema(description = "等级")
    @TableField("level")
    private Integer level;

    @Schema(description = "创造时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "最近登录")
    @TableField("last_login")
    private LocalDateTime lastLogin;

    @Schema(description = "登录次数")
    @TableField("login_count")
    private Integer loginCount;

    @Schema(description = "账号状态")
    @TableField("status")
    private Integer status;

    @Schema(defaultValue = "密码")
    @TableField("password")
    private String password;

    @Operation(description= "拿到创建时间的日期")
    public  LocalDate getCreateLocalDate(){
        return createTime.toLocalDate();
    }
}