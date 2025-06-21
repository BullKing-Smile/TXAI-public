package com.txai.servicemap.service;

import com.txai.common.constant.AmapConfigConstants;
import com.txai.common.dto.ForecastPriceDTO;
import com.txai.common.dto.ResponseResult;
import com.txai.common.response.DirectionResponse;
import com.txai.servicemap.remote.ServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ServiceMapService {

    @Value("${amap.key}")
    private String driverKey;
    private final RestTemplate restTemplate;

    @Autowired
    private ServiceClient serviceClient;


    public ServiceMapService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseResult<DirectionResponse> direct(ForecastPriceDTO forecastPriceDTO) {

        // strategy

        StringBuilder sb = new StringBuilder(AmapConfigConstants.DIRECTION_URL);
        sb.append("?origin=")
                .append(forecastPriceDTO.getDepLongitude())
                .append(",")
                .append(forecastPriceDTO.getDepLatitude())
                .append("&destination=")
                .append(forecastPriceDTO.getDestLongitude())
                .append(",")
                .append(forecastPriceDTO.getDestLatitude())
                .append("&extensions=all&output=json&key=")
                .append(driverKey);


        log.info("url: " + sb);
        ResponseEntity<String> forEntity = restTemplate.getForEntity(sb.toString(), String.class);

        System.out.println("driver direction : " + forEntity.getBody());

        JSONObject object = new JSONObject(forEntity.getBody());
        if (object.getInt("status") == 1) {
            JSONObject route = object.getJSONObject("route");
            JSONArray paths = route.getJSONArray("paths");
            if (null != paths && paths.length() > 0) {
                JSONObject directionObject = paths.getJSONObject(0);
                int distance = directionObject.getInt("distance");
                int duration = directionObject.getInt("duration");
                DirectionResponse response = new DirectionResponse();
                response.setDistance(distance);
                response.setDuration(duration);
                return ResponseResult.success(response);
            }

        }
        return ResponseResult.fail().setMessage("query direction failed or json parse failed");
    }

    /**
     * 创建服务
     *
     * @param name
     * @return
     */
    public ResponseResult add(String name) {
        return serviceClient.add(name);
    }
}
