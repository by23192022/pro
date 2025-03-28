package com.gec.ocean.controller;

import com.gec.ocean.resp.CommonResp;
import com.gec.ocean.resp.StatisticResp;
import com.gec.ocean.service.IEbookSnapshotService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lqy
 * @since 2024-10-21
 */
@RestController
@RequestMapping("/ebookSnapshot")
public class EbookSnapshotController {
    @Resource
    private IEbookSnapshotService ebookSnapshotService;

    @GetMapping("/getStatistic")
    public CommonResp getStatistic() {
        List<StatisticResp> statisticResp = ebookSnapshotService.getStatistic();
        CommonResp<List<StatisticResp>> commonResp = new CommonResp<>();
        commonResp.setContent(statisticResp);
        return commonResp;
    }

    @GetMapping("/get30Statistic")
    public CommonResp get30Statistic() {
        List<StatisticResp> statisticResp = ebookSnapshotService.get30Statistic();
        CommonResp<List<StatisticResp>> commonResp = new CommonResp<>();
        commonResp.setContent(statisticResp);
        return commonResp;
    }

}