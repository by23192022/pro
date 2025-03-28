package com.gec.ocean.service.impl;

import com.gec.ocean.entity.EbookSnapshot;
import com.gec.ocean.mapper.EbookSnapshotMapper;
import com.gec.ocean.resp.StatisticResp;
import com.gec.ocean.service.IEbookSnapshotService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lqy
 * @since 2024-10-21
 */
@Service
public class EbookSnapshotServiceImpl extends ServiceImpl<EbookSnapshotMapper, EbookSnapshot> implements IEbookSnapshotService {

    @Override
    public void genSnapshot() {
        this.baseMapper.genSnapshot();
    }

    @Override
    public List<StatisticResp> getStatistic() {
        return  this.baseMapper.getStatistic();
    }

    @Override
    public List<StatisticResp> get30Statistic() {
        return this.baseMapper.get30Statistic();
    }

}
