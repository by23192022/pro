package com.gec.ocean.controller;

import com.gec.ocean.resp.CommonResp;
import com.gec.ocean.service.IContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lqy
 * @since 2024-10-21
 */
@RestController
@RequestMapping("/content")
public class ContentController {
    @Autowired
    private IContentService contentService;

    @GetMapping("/findContent/{id}")
    public CommonResp findContent(@PathVariable Long id) {
        CommonResp<String> resp = new CommonResp<>();
        String content = contentService.findContent(id);
        resp.setContent(content);
        return resp;
    }
}
