package com.gec.ocean.mapper;

import com.gec.ocean.entity.Ebook;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 电子书 Mapper 接口
 * </p>
 *
 * @author lqy
 * @since 2024-10-21
 */
public interface EbookMapper extends BaseMapper<Ebook> {

    void increaseViewCount(Long ebookId);
}
