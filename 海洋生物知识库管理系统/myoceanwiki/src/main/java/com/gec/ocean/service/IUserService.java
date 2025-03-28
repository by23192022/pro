package com.gec.ocean.service;

import com.gec.ocean.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.ocean.rep.UserLoginReq;
import com.gec.ocean.rep.UserQueryReq;
import com.gec.ocean.rep.UserResetPasswordReq;
import com.gec.ocean.rep.UserSaveReq;
import com.gec.ocean.resp.PageResp;
import com.gec.ocean.resp.UserLoginResp;
import com.gec.ocean.resp.UserQueryResp;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lqy
 * @since 2024-10-21
 */
public interface IUserService extends IService<User> {

    PageResp<UserQueryResp> listByname(UserQueryReq req);

    void save(UserSaveReq req);

    void delete(Long id);

    void resetPassword(UserResetPasswordReq req);

    UserLoginResp login(UserLoginReq req);
}
