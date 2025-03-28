package com.gec.ocean.controller;

import com.gec.ocean.rep.CategoryQueryReq;
import com.gec.ocean.rep.CategorySaveReq;
import com.gec.ocean.resp.CategoryQueryResp;
import com.gec.ocean.resp.CommonResp;
import com.gec.ocean.resp.PageResp;
import com.gec.ocean.service.ICategoryService;
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
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @GetMapping("/list")
    public CommonResp list(@Valid CategoryQueryReq req){
        CommonResp< PageResp<CategoryQueryResp> > resp = new CommonResp<>(true, "查询成功", null);

        PageResp<CategoryQueryResp> pageResp = categoryService.listByname(req);
        resp.setContent(pageResp);

        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody CategorySaveReq req){
        CommonResp resp = new CommonResp<>(true, "保存成功", null);
        categoryService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp<>(true, "删除成功", null);
        categoryService.delete(id);
        return resp;
    }

    @GetMapping("/all")
    public CommonResp all() {
        CommonResp<List<CategoryQueryResp>> resp = new CommonResp<>(true,"查询成功",null);

        List<CategoryQueryResp> list = categoryService.all();
        resp.setContent(list);
        return resp;
    }


}
