package com.txai.servicemap.controller;

import com.txai.common.dto.ForecastPriceDTO;
import com.txai.common.dto.ResponseResult;
import com.txai.common.response.DirectionResponse;
import com.txai.servicemap.service.ServiceMapService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceMapController {
    private final ServiceMapService serviceMapService;
    public ServiceMapController(ServiceMapService serviceMapService) {
        this.serviceMapService = serviceMapService;
    }

    @PostMapping("/direction")
    public ResponseResult<DirectionResponse> direction(@RequestBody ForecastPriceDTO forecastPriceDTO) {
        return serviceMapService.direct(forecastPriceDTO);
    }
}
