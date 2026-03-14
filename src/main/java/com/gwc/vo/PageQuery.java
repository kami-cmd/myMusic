package com.gwc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageQuery {
    //每页数量
    private int pageSize;
    //页码
    private int currentPage;
    //排序字段
    private String sortField;
    //升序还是降序
    private String sortOrder;
}
