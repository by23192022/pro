package com.gec.ocean.service;

import com.gec.ocean.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.ocean.rep.CategoryQueryReq;
import com.gec.ocean.rep.CategorySaveReq;
import com.gec.ocean.resp.CategoryQueryResp;
import com.gec.ocean.resp.PageResp;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lqy
 * @since 2024-10-21
 *
 *
 *

List<CategoryQueryResp> all();

 */
public interface ICategoryService extends IService<Category> {

    PageResp<CategoryQueryResp> listByname(CategoryQueryReq req);
    void save(CategorySaveReq req);
    void delete(Long id);
    List<CategoryQueryResp> all();
}
