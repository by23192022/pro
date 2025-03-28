package com.gec.ocean.rep;

import lombok.Data;
import lombok.ToString;

/**
 * @author cr
 * @date 2023年11月13日 9:48
 * @description 分类修改或新增参数
 */
@Data
@ToString
public class CategorySaveReq {
    private Long id;

    //父id
    private Long parent;

    //名称
    private String name;

    //顺序
    private Integer sort;
}
