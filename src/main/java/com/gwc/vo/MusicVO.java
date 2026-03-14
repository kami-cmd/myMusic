package com.gwc.vo;


import lombok.Data;

@Data

public class MusicVO extends PageQuery {

    private Integer id;

    private String name;

    private String singer;

    private String size;

    private Integer type;

    private String sorageAddress;

}
