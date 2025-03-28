package com.gec.ocean.rep;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
public  class UserSaveReq {
    //主键
    private Long id;
    //登录名
    private String loginName;
    //昵称
    private String name;
    //密码
    private String password;
}