package com.gec.ocean.controller;

import com.gec.ocean.rep.DocQueryReq;
import com.gec.ocean.rep.DocSaveReq;
import com.gec.ocean.resp.CommonResp;
import com.gec.ocean.resp.DocQueryResp;
import com.gec.ocean.resp.PageResp;
import com.gec.ocean.service.IDocService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lqy
 * @since 2024-10-21
 */
@Api("Doc服务接口")
@RestController
@RequestMapping("/doc")
public class DocController {

    @Autowired
    private IDocService docService;

    @ApiOperation("查询所有数据")
    @GetMapping("/all")
    public CommonResp all() {
        //1 new响应
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>(true, "查询成功", null);
        //3 填充响应内容
        //4 调用服务层函数
        List<DocQueryResp> list = docService.all();
        resp.setContent(list);
        //2 返回响应
        return resp;
    }

    @GetMapping("/all/{ebookId}")
    public CommonResp allByEbookId(@PathVariable Long ebookId) {
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list = docService.allbyEbookId(ebookId);
        resp.setContent(list);
        return resp;
    }

    @GetMapping("/list")
    public CommonResp list(@Valid DocQueryReq req) {
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>(true,"查询成功",null);
        PageResp<DocQueryResp> pageResp = docService.listByname(req);
        resp.setContent(pageResp);
        return resp;
    }

    @ApiOperation("添加或修改数据")
    @PostMapping("/save")
    public  CommonResp save(@ApiParam("电子书对象") @Valid @RequestBody DocSaveReq req) {
        CommonResp resp = new CommonResp<>(true, "保存成功", null);
        docService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable long id) {
        CommonResp resp = new CommonResp<>(true, "删除成功", null);
        docService.delete(id);
        return resp;
    }

    //批量删除
    @GetMapping("/remove")
    public CommonResp delete(@RequestParam("ids") List<Long> ids) {
        CommonResp resp = new CommonResp<>(true,"删除成功",null);

        docService.delete(ids);
        return resp;
    }

    //文档点赞
    @GetMapping("/vote/{id}")
    public CommonResp vote(@PathVariable Long id) {
        CommonResp commonResp = new CommonResp();
        docService.vote(id);
        return commonResp;
    }


}
