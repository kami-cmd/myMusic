package com.gwc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MusicStats {

    private Long totalSongs;

    private String totalSize;

    private Long totalAdded;

    private Long totalPlays;
}
