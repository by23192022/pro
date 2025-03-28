package com.gec.ocean.service;

import com.gec.ocean.entity.Content;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lqy
 * @since 2024-10-21
 */
public interface IContentService extends IService<Content> {

    String findContent(Long id);
}
