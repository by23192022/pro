package com.gec.ocean.service;

import com.gec.ocean.entity.EbookSnapshot;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.ocean.resp.StatisticResp;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lqy
 * @since 2024-10-21
 */
public interface IEbookSnapshotService extends IService<EbookSnapshot> {

    void genSnapshot();

    List<StatisticResp> getStatistic();

    List<StatisticResp> get30Statistic();
}
