package com.gec.ocean.rep;

import lombok.Data;
import lombok.ToString;

/**
 * @author cr
 * @date 2023年11月14日 10:10
 * @description
 */
@Data
@ToString
public class DocQueryReq  extends PageReq{
    //名称
    private String name;
}
