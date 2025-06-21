package com.txai.servicedriveruser.remote;

import com.txai.common.dto.ResponseResult;
import com.txai.common.response.TerminalResponse;
import com.txai.common.response.TrackResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("service-map")
public interface ServiceMapClient {

    @RequestMapping(method = RequestMethod.POST, value = "/terminal/add")
    ResponseResult<TerminalResponse> addTerminal(@RequestParam String name, @RequestParam String desc);

    @RequestMapping(method = RequestMethod.POST, value = "/track/add")
    ResponseResult<TrackResponse> addTrack(@RequestParam String tid);
}
