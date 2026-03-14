package com.gwc.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "音乐上传趋势")
public class SongStatic {
    List<LocalDate> dates;
    List<Long> newSongs;
}
