package com.txai.apipassenger.controller;

import com.txai.apipassenger.service.ForecastPriceService;
import com.txai.common.dto.ForecastPriceDTO;
import com.txai.common.dto.ResponseResult;
import com.txai.common.response.ForecastPriceResponse;
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

        log.info("出发地经度：" + forecastPriceDTO.getDepLongitude());
        log.info("出发地纬度：" + forecastPriceDTO.getDepLatitude());
        log.info("目的地经度：" + forecastPriceDTO.getDestLongitude());
        log.info("目的地纬度：" + forecastPriceDTO.getDestLatitude());
        return forecastPriceService.forecastPrice(forecastPriceDTO);
    }
}
