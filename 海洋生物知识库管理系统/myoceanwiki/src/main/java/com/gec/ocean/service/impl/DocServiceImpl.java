package com.gec.ocean.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.ocean.entity.Content;
import com.gec.ocean.entity.Doc;
import com.gec.ocean.exception.BusinessException;
import com.gec.ocean.exception.BusinessExceptionCode;
import com.gec.ocean.mapper.DocMapper;
import com.gec.ocean.mapper.EbookMapper;
import com.gec.ocean.rep.DocQueryReq;
import com.gec.ocean.rep.DocSaveReq;
import com.gec.ocean.resp.DocQueryResp;
import com.gec.ocean.resp.PageResp;
import com.gec.ocean.service.IContentService;
import com.gec.ocean.service.IDocService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.ocean.utils.CopyUtil;
import com.gec.ocean.utils.RedisUtil;
import com.gec.ocean.utils.RequestContext;
import com.gec.ocean.utils.SnowFlake;
//import org.apache.commons.lang3.ObjectUtils;
import com.gec.ocean.websocket.WebSocketServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lqy
 * @since 2024-10-21
 */
@Service
public class DocServiceImpl extends ServiceImpl<DocMapper, Doc> implements IDocService {

    @Autowired
    private SnowFlake snowFlake;

    @Autowired
    IContentService contentService;

    //该电子书阅读数+1
    @Autowired(required = false)
    private EbookMapper ebookMapper;

    //点赞功能优化
    @Autowired(required = false)
    private RedisUtil redisUtil;

    //3.6.3 完成点赞通知功能
    @Autowired
    private WebSocketServer webSocketServer;

    //3.8.2 Springboot集成RabbitMQ
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    //6 实现函数
    @Override
    public PageResp<DocQueryResp> listByname(DocQueryReq req) {
        QueryWrapper<Doc> queryWrapper = new QueryWrapper<Doc>();
        queryWrapper.like(StringUtils.isNotBlank(req.getName()),"name",req.getName());

        Page<Doc> page = new Page<>(req.getPage(), req.getSize());
        page = this.baseMapper.selectPage(page, queryWrapper);

        List<DocQueryResp> resps = CopyUtil.copyList(page.getRecords(),DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp<>();
        pageResp.setList(resps);
        pageResp.setTotal(page.getTotal());
        return pageResp;
    }

    @Override
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);

        Content content = CopyUtil.copy(req, Content.class);

        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增文档
            long id = snowFlake.nextId();
            doc.setId(id);
            doc.setViewCount(0);//默认查看点赞为0
            doc.setVoteCount(0);
            this.baseMapper.insert(doc);

            //新增内容
            content.setId(id);
            contentService.save(content);

        } else {
            this.baseMapper.updateById(doc);

            boolean b = contentService.updateById(content);//更新内容
            if(!b){//根据id更新失败,执行新增功能
                contentService.save(content);
            }
        }
    }

    @Override
    public void delete(long id) {
        this.baseMapper.deleteById(id);
    }
    //批量删除
    @Override
    public void delete(List<Long> ids) {
        this.baseMapper.deleteBatchIds(ids);
    }

    @Override
    public List<DocQueryResp> all() {
        QueryWrapper<Doc> queryWrapper = new QueryWrapper<Doc>();
        queryWrapper.orderByAsc("sort");
        List<Doc> categories = this.baseMapper.selectList(queryWrapper);

        List<DocQueryResp> list = CopyUtil.copyList(categories, DocQueryResp.class);
        return list;
    }

    @Override
    public List<DocQueryResp> allbyEbookId(Long ebookId) {

        //该电子书阅读数+1
        ebookMapper.increaseViewCount(ebookId);


        QueryWrapper<Doc> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ebook_id",ebookId).orderByAsc("sort");
        List<Doc> doclist = this.baseMapper.selectList(queryWrapper);

        // 列表复制
        List<DocQueryResp> list = CopyUtil.copyList(doclist, DocQueryResp.class);
        return list;
    }

    //文档点赞、点赞功能优化
    @Override
    public void vote(Long id) {

        //key为  DOC_VOTE_123123123_0:0:0:0:0:0:0:1
        String key ="DOC_VOTE_"+id+"_"+ RequestContext.getRemoteAddr();

        // 设为3600*24 表示24小时内 同一用户 能点赞一次 该文档
        //if(redisUtil.validateRepeat(key,3600*24)){
        if(redisUtil.validateRepeat(key,10)){
            this.baseMapper.increaseVoteCount(id);
        }else{
                //× LOG.info("您已点赞过");
            //rocketMQTemplate.convertAndSend("VOTE_TOPIC", "您已点赞过");
            //webSocketServer.sendInfo("您已点赞过...");
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);

        }


        //3.6.3 完成点赞通知功能
        //查询点赞文档信息
        Doc doc = this.baseMapper.selectById(id);
        //webSocketServer.sendInfo("【您的文档 " + doc.getName() + "】被点赞！");
        //↓ webSocketServer.sendInfo被放到 VoteTopicConsumer.java | @RocketMQMessageListener 里调用了。
        //↓
        //3.8.2 Springboot集成RabbitMQ
        String logId = MDC.get("LOG_ID");
        //参数1 发送队列    参数2消息内容
        rocketMQTemplate.convertAndSend("VOTE_TOPIC", "您的文档【" + doc.getName() + "】被点赞！");

    }

    //定时更新电子书信息
    @Override
    public void updateEbookInfo() {
        this.baseMapper.updateEbookInfo();
    }

}
