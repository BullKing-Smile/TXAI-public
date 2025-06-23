package com.txai.serviceprice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.txai.common.constant.CommonStatusEnum;
import com.txai.common.dto.ForecastPriceDTO;
import com.txai.common.dto.PriceRule;
import com.txai.common.dto.ResponseResult;
import com.txai.common.response.DirectionResponse;
import com.txai.common.response.ForecastPriceResponse;
import com.txai.common.util.BigDecimalUtils;
import com.txai.serviceprice.mapper.PriceRuleMapper;
import com.txai.serviceprice.remote.ServiceMapClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ForecastPriceService {

    private final ServiceMapClient serviceMapClient;
    private final PriceRuleMapper priceRuleMapper;

    public ForecastPriceService(ServiceMapClient serviceMapClient,
                                PriceRuleMapper priceRuleMapper) {
        this.serviceMapClient = serviceMapClient;
        this.priceRuleMapper = priceRuleMapper;
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
        // 计价规则
//        Map<String, Object> priceQueryMap = new HashMap<>();
//        priceQueryMap.put("city_code", "110000");
//        priceQueryMap.put("vehicle_type", "1");

        // 根据fare_version排序
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("city_code", forecastPriceDTO.getCityCode());
        wrapper.eq("vehicle_type", forecastPriceDTO.getVehicleType());
        wrapper.orderByDesc("fare_version");
        List<PriceRule> priceRules = priceRuleMapper.selectList(wrapper);

        if (priceRules.size() == 0) {
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_NOT_EXISTS);
        }

        // 获得到 计价规则数据对象
        PriceRule priceRule = priceRules.get(0);

        // 根据距离计算价格

        double price = getPrice(directionResponse.getDistance(), directionResponse.getDuration(), priceRule);
        ForecastPriceResponse response = new ForecastPriceResponse();
        response.setPrice(price);
        return ResponseResult.success(response);
    }


    /**
     * 根据距离、时长 和计价规则，计算最终价格
     * @param distance  距离
     * @param duration  时长
     * @param priceRule 计价规则
     * @return
     */
    public double getPrice(Integer distance , Integer duration,PriceRule priceRule){
        double price = 0;

        // 起步价
        double startFare = priceRule.getStartFare();
        price = BigDecimalUtils.add(price,startFare);

        // 里程费
        // 总里程 m
        double distanceMile = BigDecimalUtils.divide(distance,1000);
        // 起步里程
        double startMile = (double)priceRule.getStartMile();
        double distanceSubtract = BigDecimalUtils.substract(distanceMile,startMile);
        // 最终收费的里程数 km
        double mile = distanceSubtract<0?0:distanceSubtract;
        // 计程单价 元/km
        double unitPricePerMile = priceRule.getUnitPricePerMile();
        // 里程价格
        double mileFare = BigDecimalUtils.multiply(mile,unitPricePerMile);
        price = BigDecimalUtils.add(price,mileFare);

        // 时长费
        // 时长的分钟数
        double timeMinute = BigDecimalUtils.divide(duration,60);
        // 计时单价
        double unitPricePerMinute = priceRule.getUnitPricePerMinute();

        // 时长费用
        double timeFare = BigDecimalUtils.multiply(timeMinute,unitPricePerMinute);
        price = BigDecimalUtils.add(price,timeFare);

        BigDecimal priceBigDecimal = new BigDecimal(price);
        priceBigDecimal = priceBigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP);

        return priceBigDecimal.doubleValue();
    }

}
