package com.gec.ocean.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class EbookQueryResp {
    /*解决Vue超过16位数字解析精度丢失问题:
    当我们新增一条数据时,数据库中id为雪花算法生成的`378111468182310912`
    而在前端页面显示的为`378111468182310900`,精度丢失。
     */
    //把id类型改为String
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String name;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long category1Id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long category2Id;

    private String description;

    private String cover;

    private Integer docCount;

    private Integer viewCount;

    private Integer voteCount;
}