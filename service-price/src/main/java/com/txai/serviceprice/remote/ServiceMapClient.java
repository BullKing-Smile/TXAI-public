package com.txai.serviceprice.remote;

import com.txai.common.dto.ForecastPriceDTO;
import com.txai.common.dto.ResponseResult;
import com.txai.common.response.DirectionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "service-map")
public interface ServiceMapClient {

    @PostMapping("/direction")
    ResponseResult<DirectionResponse> direction(@RequestBody ForecastPriceDTO forecastPriceDTO);

}
