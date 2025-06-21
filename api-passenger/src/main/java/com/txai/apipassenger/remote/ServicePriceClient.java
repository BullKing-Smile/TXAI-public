package com.txai.apipassenger.remote;

import com.txai.common.dto.ForecastPriceDTO;
import com.txai.common.dto.ResponseResult;
import com.txai.common.response.ForecastPriceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "service-price")
public interface ServicePriceClient {

    @PostMapping("/forecast-price")
    ResponseResult<ForecastPriceResponse> forecastPrice(@RequestBody ForecastPriceDTO forecastPriceDTO);

}
