package com.gwc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeStaticResult {

    private Long yesterdayLoginUsers;

    private Double loginUsersChange;

    private Long newSongs;

    private Double newSongsChange;

    private Long totalUsers;

    private Long totalSongs;

    private Long yesterdayNewUsers;

    private Double newUsersChange;

    private Long peakHour;

    private Long peakHourUsers;
}
