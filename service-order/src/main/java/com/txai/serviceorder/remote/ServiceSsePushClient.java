package com.txai.serviceorder.remote;

import com.txai.common.request.PushRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("service-sse-push")
public interface ServiceSsePushClient {

    @PostMapping(value = "/push")
    String push(@RequestBody PushRequest pushRequest);
}
