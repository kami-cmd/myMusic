package com.gwc.vo;

import lombok.Data;

import java.time.LocalDate;


@Data
public class UserVO extends PageQuery {

    private Long id;

    private String userName;

    private String nickName;

    private String phone;

    private String email;

    private Integer level;

    private Integer status;

    private LocalDate createTimeStart;

    private LocalDate createTimeEnd;

    private  Integer gender;

    private String password;

    private String oldPassword;

}
