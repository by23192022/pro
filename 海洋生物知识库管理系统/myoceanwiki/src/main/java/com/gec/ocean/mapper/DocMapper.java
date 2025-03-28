package com.gec.ocean.mapper;

import com.gec.ocean.entity.Doc;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lqy
 * @since 2024-10-21
 */
public interface DocMapper extends BaseMapper<Doc> {

    void increaseViewCount(Long id);

    //文档点赞
    void increaseVoteCount(Long id);

    //定时更新电子书信息
    void updateEbookInfo();
}
