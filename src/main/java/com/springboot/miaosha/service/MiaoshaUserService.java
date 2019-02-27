package com.springboot.miaosha.service;

import com.springboot.miaosha.dao.MiaoshaUserDao;
import com.springboot.miaosha.domain.MiaoshaUser;
import com.springboot.miaosha.exception.GlobalException;
import com.springboot.miaosha.redis.MiaoshaUserKey;
import com.springboot.miaosha.redis.RedisService;
import com.springboot.miaosha.result.CodeMsg;
import com.springboot.miaosha.util.Md5Util;
import com.springboot.miaosha.util.UUIDUtil;
import com.springboot.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: lei
 * @Description:
 * @Date: Created in 2019-02-27 19:42
 */

@Service
public class MiaoshaUserService {

    private static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    public MiaoshaUser getById(long id) {
        return miaoshaUserDao.getById(id);
    }

    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }

        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();

        //判断手机号是否存在
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = Md5Util.formPassToDBPass(formPass, saltDB);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        //生成cookie
        String token = UUIDUtil.uuid();
        redisService.set(MiaoshaUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSecounds());
        cookie.setPath("/");

        response.addCookie(cookie);
        return true;
    }
}
