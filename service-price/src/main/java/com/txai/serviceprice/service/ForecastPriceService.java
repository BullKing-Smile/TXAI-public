package com.txai.serviceprice.service;

import com.txai.common.dto.ForecastPriceDTO;
import com.txai.common.dto.ResponseResult;
import com.txai.common.response.DirectionResponse;
import com.txai.common.response.ForecastPriceResponse;
import com.txai.serviceprice.remote.ServiceMapClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ForecastPriceService {

    private final ServiceMapClient serviceMapClient;

    public ForecastPriceService(ServiceMapClient serviceMapClient) {
        this.serviceMapClient = serviceMapClient;
    }

    public ResponseResult<ForecastPriceResponse> forecastPrice(ForecastPriceDTO forecastPriceDTO) {

        log.info("出发地经度：" + forecastPriceDTO.getDepLongitude());
        log.info("出发地纬度：" + forecastPriceDTO.getDepLatitude());
        log.info("目的地经度：" + forecastPriceDTO.getDestLongitude());
        log.info("目的地纬度：" + forecastPriceDTO.getDestLatitude());

        //TODO 调用地图服务 查询距离
        ResponseResult<DirectionResponse> direction = serviceMapClient.direction(forecastPriceDTO);
        DirectionResponse directionResponse = direction.getData();
        if (null != directionResponse) {
            log.info("distance=" + directionResponse.getDistance());
            log.info("duration=" + directionResponse.getDuration());
        } else {
            log.warn("get direction info failed");
        }
        // TODO 计价规则


        // TODO 根据距离计算价格
        ForecastPriceResponse response = new ForecastPriceResponse();
        response.setPrice(12.89f);
        return ResponseResult.success(response);
    }
}
