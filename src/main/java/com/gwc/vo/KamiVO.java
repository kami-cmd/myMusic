package com.gwc.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class KamiVO extends PageQuery {
    private String verificationCode;

    private String account;

    private String kamiName;

    private String phone;

    private String email;

    @DateTimeFormat(style = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(style = "yyyy-MM-dd")
    private LocalDate endDate;
    @DateTimeFormat(style = "yyyy-MM-dd")
    private LocalDate birthday;

}
