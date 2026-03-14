package com.gwc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterVO {

    private String userName;

    private String nickName;

    private String phone;

    private String email;

    private String code;

    private Integer gender;

    private String password;

    private String avatar;
}
