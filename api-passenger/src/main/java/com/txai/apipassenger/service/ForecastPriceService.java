package com.txai.apipassenger.service;

import com.txai.apipassenger.response.ForecastPriceResponse;
import com.txai.common.dto.ForecastPriceDTO;
import com.txai.common.dto.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Service
@Slf4j
public class ForecastPriceService {

    public ResponseResult<ForecastPriceResponse> forecastPrice(String depLongitude, String depLatitude, String destLongitude, String despLatitude) {

        ForecastPriceResponse response = new ForecastPriceResponse();
        response.setPrice(12.89f);
        return ResponseResult.success(response);
    }
}
