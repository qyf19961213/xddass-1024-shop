package net.xdclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.enums.SendCodeEnum;
import net.xdclass.mapper.UserMapper;
import net.xdclass.model.UserDO;
import net.xdclass.request.UserLoginRequest;
import net.xdclass.request.UserRegisterRequest;
import net.xdclass.service.NotifyService;
import net.xdclass.service.UserService;
import net.xdclass.util.CommonUtil;
import net.xdclass.util.JsonData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.Md5Crypt;

import java.util.Date;
import java.util.List;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

@Service
@Slf4j
public class UserServiceImpl implements UserService {


    @Autowired
    private NotifyService notifyService;


    @Autowired
    private UserMapper userMapper;

    /**
     * 用户注册
     * * 邮箱验证码验证
     * * 密码加密（TODO）
     * * 账号唯一性检查(TODO)
     * * 插入数据库
     * * 新注册用户福利发放(TODO)
     *
     * @param registerRequest
     * @return
     */
    @Override
    public JsonData register(UserRegisterRequest registerRequest) {

        boolean checkCode = false;
        //校验验证码
        if (StringUtils.isNotBlank(registerRequest.getMail())) {
            checkCode = notifyService.checkCode(SendCodeEnum.USER_REGISTER, registerRequest.getMail(), registerRequest.getCode());
        }

        if (!checkCode) {
            return JsonData.buildResult(BizCodeEnum.CODE_ERROR);
        }

        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(registerRequest, userDO);

        userDO.setCreateTime(new Date());

        //设置密码 TODO
        //userDO.setPwd(registerRequest.getPwd());
        //生成密钥 盐
        userDO.setSecret("$1$" + CommonUtil.getStringNumRandom(8));

        //密码+盐处理
        String cryptPwd = Md5Crypt.md5Crypt(registerRequest.getPwd().getBytes(), userDO.getSecret());
        userDO.setPwd(cryptPwd);
        //账号唯一性检查  TODO

        if (checkUnique(userDO.getMail())) {
            int rows = userMapper.insert(userDO);
            log.info("rows:{},注册成功:{}", rows, userDO.toString());

            //新用户注册成功，初始化信息，发放福利等 TODO
            userRegisterInitTask(userDO);
            return JsonData.buildSuccess();
        } else {
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_REPEAT);
        }

    }

    @Override
    public JsonData login(UserLoginRequest loginRequest) {
        List<UserDO> userDOList = userMapper.selectList(new QueryWrapper<UserDO>().eq("mail", loginRequest.getMail()));
        if (userDOList != null && userDOList.size() > 0) {
            //已经注册
            UserDO userDO = userDOList.get(0);
            String cryptPwd = Md5Crypt.md5Crypt(loginRequest.getPwd().getBytes(), userDO.getSecret());
            if (cryptPwd.equals(userDO.getPwd())) {
                //登录成功，生成token TODO
                return null;
            } else {
                return JsonData.buildResult(BizCodeEnum.ACCOUNT_PWD_ERROR);
            }
        } else {
            //未注册
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_PWD_ERROR);
        }
    }

    /**
     * 校验用户账号唯一
     *
     * @param mail
     * @return
     */
    private boolean checkUnique(String mail) {
        QueryWrapper queryWrapper = new QueryWrapper<UserDO>().eq("mail", mail);
        List<UserDO> list = userMapper.selectList(queryWrapper);
        return list.size() > 0 ? false : true;
    }


    /**
     * 用户注册，初始化福利信息 TODO
     *
     * @param userDO
     */
    private void userRegisterInitTask(UserDO userDO) {

    }


}
