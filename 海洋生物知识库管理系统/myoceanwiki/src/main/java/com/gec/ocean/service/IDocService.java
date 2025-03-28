package com.gec.ocean.service;

import com.gec.ocean.entity.Doc;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.ocean.rep.DocQueryReq;
import com.gec.ocean.rep.DocSaveReq;
import com.gec.ocean.resp.DocQueryResp;
import com.gec.ocean.resp.PageResp;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lqy
 * @since 2024-10-21
 */
public interface IDocService extends IService<Doc> {

    //5 声明函数

    PageResp<DocQueryResp> listByname(DocQueryReq req);

    void save(DocSaveReq req);

    void delete(long id);
    void delete(List<Long> ids);    //批量删除


    List<DocQueryResp> all();

    List<DocQueryResp> allbyEbookId(Long ebookId);

    //文档点赞
    void vote(Long id);

    //定时更新电子书信息
    void updateEbookInfo();
}
