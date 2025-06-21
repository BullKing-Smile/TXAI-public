package com.txai.serviceprice.controller;

import com.txai.common.dto.ForecastPriceDTO;
import com.txai.common.dto.ResponseResult;
import com.txai.common.response.ForecastPriceResponse;
import com.txai.serviceprice.service.ForecastPriceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ForecastPriceController {

    private ForecastPriceService forecastPriceService;
    public ForecastPriceController(ForecastPriceService forecastPriceService) {
        this.forecastPriceService = forecastPriceService;
    }


    @PostMapping("/forecast-price")
    public ResponseResult<ForecastPriceResponse> forecastPrice(@RequestBody ForecastPriceDTO forecastPriceDTO) {
        return forecastPriceService.forecastPrice(forecastPriceDTO);
    }
}
