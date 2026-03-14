package com.gwc.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginTimeResult {

    private Long userId;

    private dateRange dateRange;

    private Integer totalLoginDays;

    private Long totalLoginTimes;

    private Double avgDailyLoginTimes;

    private List<everyData> dailyData;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class dateRange {
        @DateTimeFormat(style = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(style = "yyyy-MM-dd")
        private LocalDate endDate;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class everyData {
        @DateTimeFormat(style = "yyyy-MM-dd")
        private LocalDate date;

        private Long count;

        private List<LocalDateTime> loginTimes;
    }
}




