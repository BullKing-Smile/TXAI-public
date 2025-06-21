package com.txai.apipassenger.service;

import com.txai.apipassenger.remote.ServicePriceClient;
import com.txai.common.dto.ForecastPriceDTO;
import com.txai.common.dto.ResponseResult;
import com.txai.common.response.ForecastPriceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ForecastPriceService {
    private final ServicePriceClient servicePriceClient;

    public ForecastPriceService(ServicePriceClient servicePriceClient) {
        this.servicePriceClient = servicePriceClient;
    }

    public ResponseResult<ForecastPriceResponse> forecastPrice(ForecastPriceDTO forecastPriceDTO) {

        return servicePriceClient.forecastPrice(forecastPriceDTO);
    }
}
