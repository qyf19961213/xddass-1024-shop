package net.xdclass.service;

import net.xdclass.model.UserDO;
import com.baomidou.mybatisplus.extension.service.IService;
import net.xdclass.request.UserLoginRequest;
import net.xdclass.request.UserRegisterRequest;
import net.xdclass.util.JsonData;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qyf
 * @since 2023-05-04
 */
public interface UserService {

    /**
     * 用户注册
     * @param registerRequest
     * @return
     */
    JsonData register(UserRegisterRequest registerRequest);

    /**
     * 用户登录
     * @param loginRequest
     * @return
     */
    JsonData login(UserLoginRequest loginRequest);
}
