package com.gec.ocean.rep;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.validation.constraints.NotNull;

@Data
@ToString
public  class EbookSaveReq {
    private Long id;

    @NotNull(message = "【名称】不能为空")
    private String name;

    private Long category1Id;

    private Long category2Id;

    private String description;

    private String cover;

    private Integer docCount = 0;    //默认值为0

    private Integer viewCount = 0;    //默认值为0

    private Integer voteCount = 0;    //默认值为0
    /*
    private Integer docCount;

    private Integer viewCount;

    private Integer voteCount;
     */
}