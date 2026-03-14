package com.gwc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserStats {
    //总
    private Long totalUsers;
    //活跃(近7天登录)
    private Long activeUsers;
    //新
    private Long newUsersToday;
    //vip
    private Long vipUsers;

}
