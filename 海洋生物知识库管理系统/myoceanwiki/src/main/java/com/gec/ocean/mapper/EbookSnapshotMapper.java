package com.gec.ocean.mapper;

import com.gec.ocean.entity.EbookSnapshot;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gec.ocean.resp.StatisticResp;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lqy
 * @since 2024-10-21
 */
public interface EbookSnapshotMapper extends BaseMapper<EbookSnapshot> {

    void genSnapshot();

    List<StatisticResp> getStatistic();

    List<StatisticResp> get30Statistic();
}
