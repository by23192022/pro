package com.gec.ocean.service;

import com.gec.ocean.entity.Ebook;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.ocean.rep.EbookQueryReq;
import com.gec.ocean.rep.EbookSaveReq;
import com.gec.ocean.resp.EbookQueryResp;
import com.gec.ocean.resp.PageResp;

import java.util.List;

/**
 * <p>
 * 电子书 服务类
 * </p>
 *
 * @author lqy
 * @since 2024-10-21
 */
public interface IEbookService extends IService<Ebook> {

    //原public List<EbookQueryResp> listByname(EbookQueryReq req);

    PageResp<EbookQueryResp> listByname(EbookQueryReq req);

    void save(EbookSaveReq req);

    void delete(Long id);
}
