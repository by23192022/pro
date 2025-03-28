package com.gec.ocean.controller;

import com.alibaba.fastjson.JSONObject;
import com.gec.ocean.rep.UserLoginReq;
import com.gec.ocean.rep.UserQueryReq;
import com.gec.ocean.rep.UserResetPasswordReq;
import com.gec.ocean.rep.UserSaveReq;
import com.gec.ocean.resp.CommonResp;
import com.gec.ocean.resp.PageResp;
import com.gec.ocean.resp.UserLoginResp;
import com.gec.ocean.resp.UserQueryResp;
import com.gec.ocean.service.IUserService;
import com.gec.ocean.utils.SnowFlake;
//import org.mybatis.logging.Logger;
//import org.mybatis.logging.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lqy
 * @since 2024-10-21
 */
@RestController
@RequestMapping("/user")
public class UserController {
    //3.5.3 实现登录功能
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private SnowFlake snowFlake;
    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private IUserService userService;

    @GetMapping("/list")
    public CommonResp list(@Valid UserQueryReq req){
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>(true,"查询成功",null);
        PageResp<UserQueryResp> pageResp = userService.listByname(req);
        resp.setContent(pageResp);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody UserSaveReq req){
        //3.3.2 后端密码加密存储
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));

        CommonResp resp = new CommonResp<>(true,"保存成功",null);
        userService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp<>(true,"删除成功",null);
        userService.delete(id);
        return resp;
    }

    @PostMapping("resetPassword")
    public CommonResp resetPassword(@Valid @RequestBody UserResetPasswordReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>(true,"成功",null);
        userService.resetPassword(req);
        return resp;
    }

    //登录
    @PostMapping("/login")
    public CommonResp login(@Valid @RequestBody UserLoginReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp = new CommonResp<>();
        UserLoginResp userLoginResp = userService.login(req);

        Long token = snowFlake.nextId();
        LOG.info("生成单点登录token：{}，并放入redis中", token);
        userLoginResp.setToken(token.toString());
        redisTemplate.opsForValue().set(token.toString(), JSONObject.toJSONString(userLoginResp), 3600 * 24, TimeUnit.SECONDS);

        resp.setContent(userLoginResp);
        return resp;
    }

    //退出登录
    @GetMapping("/logout/{token}")
    public CommonResp logout(@PathVariable String token) {
        CommonResp resp = new CommonResp<>();
        redisTemplate.delete(token);
        LOG.info("从redis中删除token: {}", token);
        return resp;
    }






}
