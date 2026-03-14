package com.gwc.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description="新增用户和活跃的趋势")
public class UserStatic {

    List<Integer> newUsers;

    List<Integer> activeUser;
}
