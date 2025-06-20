package com.txai.apipassenger.remote;

import com.txai.common.dto.PassengerUser;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.VerificationCodeCheckDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "service-passenger-user")
public interface ServicePassengerUserClient {
    @RequestMapping(method = RequestMethod.GET, value = "/loginOrRegister")
    ResponseResult loginOrRegister(@RequestBody VerificationCodeCheckDTO verificationCodeCheckDTO);

    @RequestMapping(method = RequestMethod.GET, value = "/user/{passengerPhone}")
    ResponseResult<PassengerUser> userInfo(@PathVariable("passengerPhone") String passengerPhone);

}
