package com.gwc.service;

import com.gwc.entity.HomeStaticResult;
import com.gwc.entity.SongStatic;
import com.gwc.entity.UserStatic;

import java.util.List;

public interface HomeStaticService {
    HomeStaticResult overview();

    UserStatic userTrend();

    List loginTrend();

    SongStatic songTrend();

    int[][] heatmap();
}
