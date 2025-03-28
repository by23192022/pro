package com.gec.ocean.rep;

import lombok.Data;
import lombok.ToString;

/**
 * @author cr
 * @date 2023年11月13日 9:45
 * @description 分类参数
 */
@Data
@ToString
public class CategoryQueryReq extends PageReq  {
    //名称
    private String name;
}
