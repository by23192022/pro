package com.gec.ocean.controller;

import com.gec.ocean.entity.Ebook;
import com.gec.ocean.rep.EbookQueryReq;
import com.gec.ocean.rep.EbookSaveReq;
import com.gec.ocean.resp.CommonResp;
import com.gec.ocean.resp.EbookQueryResp;
import com.gec.ocean.resp.PageResp;
import com.gec.ocean.service.IEbookService;
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
 * 电子书 前端控制器
 * </p>
 *
 * @author lqy
 * @since 2024-10-21
 */
@Api("Ebook服务接口")   //Swagger声明类加@Api注解，说明文本
@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Autowired
    private IEbookService ebookService;


    @GetMapping("/list")
    public CommonResp list(@Valid EbookQueryReq req) {
        CommonResp< PageResp<EbookQueryResp> > resp = new CommonResp<>(true,"查询成功",null);

        PageResp<EbookQueryResp> pageResp = ebookService.listByname(req);

        resp.setContent(pageResp);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody EbookSaveReq req) {
        CommonResp resp = new CommonResp<>(true,"成功",null);
        ebookService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        ebookService.delete(id);
        return resp;
    }


    @ApiOperation("查询所有数据")
    @GetMapping("/listAll")
    public CommonResp listAll() {
        CommonResp resp = new CommonResp<>(true, "查询成功", null);
        List<Ebook> list = ebookService.list();         //返回内容，JSON数据类型
        resp.setContent(list);
        return resp;
    }
     /* 不封装响应类CommonResp写法
    public List<Ebook> listAll() {
        return ebookService.list();
    }
     */

    @ApiOperation("根据id查询数据")
    @GetMapping("/findById{id}")
    public CommonResp findById(@PathVariable long id) {
        CommonResp resp = new CommonResp<>(true, "查询成功", null);
        Ebook ebook = ebookService.getById(id);
        resp.setContent(ebook);
        return resp;
    }

    /*
    @ApiOperation("添加或修改数据")
    @PostMapping("/save___")
    public CommonResp save___(@ApiParam("电子书对象") @RequestBody Ebook ebook) {
        CommonResp resp = new CommonResp<>(true, "保存成功", null);
        boolean result = ebookService.saveOrUpdate(ebook);
        if(!result) {
            resp.setSuccess(false);
            resp.setMessage("保存失败");
        }
        return resp;
    }

    @ApiOperation("根据id删除数据")
    @DeleteMapping("/delete/{id}___")
    public CommonResp delete___(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>(true, "删除成功", null);
        boolean result = ebookService.removeById(id);
        if(!result) {
            resp.setSuccess(false);
            resp.setMessage("删除失败");
        }
        return resp;
    }

     */
}
