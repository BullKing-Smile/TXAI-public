package com.txai.apipassenger.service;

import com.txai.apipassenger.remote.ServicePassengerUserClient;
import com.txai.common.constant.CommonStatusEnum;
import com.txai.common.dto.PassengerUser;
import com.txai.common.dto.ResponseResult;
import com.txai.common.dto.TokenResult;
import com.txai.common.util.JwtUtils;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final ServicePassengerUserClient servicePassengerUserClient;
    public UserService(ServicePassengerUserClient servicePassengerUserClient) {
        this.servicePassengerUserClient = servicePassengerUserClient;
    }

    public ResponseResult<PassengerUser> getUserInfoByAccessToken(String accessToken) {

        TokenResult tokenResult = JwtUtils.parseToken(accessToken);
        ResponseResult<PassengerUser> result = servicePassengerUserClient.userInfo(tokenResult.getPhone());
        if (null != result && null != result.getData()) {
            return ResponseResult.success(result.getData());
        }
        return ResponseResult.fail(CommonStatusEnum.USER_NOT_FOUND);
    }
}
