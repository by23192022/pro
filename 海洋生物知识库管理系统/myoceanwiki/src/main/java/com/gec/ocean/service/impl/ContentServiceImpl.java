package com.gec.ocean.service.impl;

import com.gec.ocean.entity.Content;
import com.gec.ocean.mapper.ContentMapper;
import com.gec.ocean.mapper.DocMapper;
import com.gec.ocean.service.IContentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lqy
 * @since 2024-10-21
 */
@Service
public class ContentServiceImpl extends ServiceImpl<ContentMapper, Content> implements IContentService {

    @Autowired(required = false)
    DocMapper docMapper;

    @Override
    public String findContent(Long id) {
        Content content = this.baseMapper.selectById(id);

        docMapper.increaseViewCount(id);

        if(content !=null){
            return content.getContent();
        }
        return "";
    }
}
