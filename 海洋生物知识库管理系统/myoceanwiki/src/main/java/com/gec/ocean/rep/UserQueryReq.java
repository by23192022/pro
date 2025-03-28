package com.gec.ocean.rep;

import lombok.Data;
import lombok.ToString;

/**
 * @author cr
 * @date 2023年11月09日 14:29
 * @description
 */
@Data
@ToString
public class UserQueryReq extends PageReq {
    //主键
    private Long id;
    //登录名
    private String loginName;
    //昵称
    private String  name;
}
