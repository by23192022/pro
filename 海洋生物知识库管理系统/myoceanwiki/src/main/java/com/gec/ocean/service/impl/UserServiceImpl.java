package com.gec.ocean.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.ocean.entity.User;
import com.gec.ocean.exception.BusinessException;
import com.gec.ocean.exception.BusinessExceptionCode;
import com.gec.ocean.mapper.UserMapper;
import com.gec.ocean.rep.UserLoginReq;
import com.gec.ocean.rep.UserQueryReq;
import com.gec.ocean.rep.UserResetPasswordReq;
import com.gec.ocean.rep.UserSaveReq;
import com.gec.ocean.resp.PageResp;
import com.gec.ocean.resp.UserLoginResp;
import com.gec.ocean.resp.UserQueryResp;
import com.gec.ocean.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.ocean.utils.CopyUtil;
import com.gec.ocean.utils.SnowFlake;
//import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
//import org.mybatis.logging.Logger;
//import org.mybatis.logging.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.util.ObjectUtils;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lqy
 * @since 2024-10-21
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    //3.5.3 实现登录功能
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private SnowFlake snowFlake;

    @Override
    public PageResp<UserQueryResp> listByname(UserQueryReq req) {
        //表
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        //第一个参数：该参数是一个布尔类型，只有该参数是true时，才将like条件拼接到sql中；本例中，如果name字段不为空，则拼接name字段的like查询条件；
        queryWrapper.like(StringUtils.isNotBlank(req.getName()), "name", req.getName());
        queryWrapper.or().like(StringUtils.isNotBlank(req.getLoginName()), "login_name", req.getLoginName());

        //页面
        Page<User> page =new Page<>(req.getPage(),req.getSize());
        //页面，符合条件的表数据
        page = this.baseMapper.selectPage(page, queryWrapper);
        //List分页数据
        List<UserQueryResp> resps = CopyUtil.copyList(page.getRecords(),UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp<>();
        pageResp.setList(resps);
        pageResp.setTotal(page.getTotal());

        return pageResp;
    }

    @Override
    public void save(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);
        if(ObjectUtils.isEmpty(req.getId())){

            //
            //判断用户名是否已经存在
            User one = this.baseMapper.selectOne(new QueryWrapper<User>().eq("login_name", req.getLoginName()));
            if(ObjectUtils.isEmpty(one)) {

                long id = snowFlake.nextId();
                user.setId(id);
                this.baseMapper.insert(user);
            }else{
                //
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }

        } else {
            //
            user.setLoginName(null);//避免绕过前端 修改登录名
            //
            user.setPassword(null);//用户存在的话，禁止修改密码

            this.baseMapper.updateById(user);
        }
    }

    @Override
    public void delete(Long id) {
        this.baseMapper.deleteById(id);
    }

    @Override
    public void resetPassword(UserResetPasswordReq req) {
        User user = CopyUtil.copy(req, User.class);
        this.baseMapper.updateById(user);
    }

    @Override
    public UserLoginResp login(UserLoginReq req) {
        //1.根据用户名查询用户信息
        User user = this.baseMapper.selectOne(new QueryWrapper<User>().eq("login_name", req.getLoginName()));
        if (ObjectUtils.isEmpty(user)) {
            // 用户名不存在
            LOG.info("用户名不存在, {}", req.getLoginName());
            throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
        } else {
            if (user.getPassword().equals(req.getPassword())) {
                // 登录成功
                UserLoginResp userLoginResp = CopyUtil.copy(user, UserLoginResp.class);
                return userLoginResp;
            } else {
                // 密码不对
                LOG.info("密码不对, 输入密码：{}, 数据库密码：{}", req.getPassword(), user.getPassword());
                throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
            }
        }
    }


}