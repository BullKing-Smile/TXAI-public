package com.txai.apidriver.service;

import com.txai.apidriver.remote.ServiceDriverUserClient;
import com.txai.apidriver.remote.ServiceMapClient;
import com.txai.common.dto.Car;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.ApiDriverPointRequest;
import com.txai.common.request.PointRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointService {

    @Autowired
    private ServiceMapClient serviceMapClient;

    @Autowired
    private ServiceDriverUserClient serviceDriverUserClient;

    public ResponseResult upload(ApiDriverPointRequest apiDriverPointRequest) {
        // 获取carId
        Long carId = apiDriverPointRequest.getCarId();

        // 通过carId 获取 tid、trid，调用 service-driver-user的接口
        ResponseResult<Car> carById = serviceDriverUserClient.getCarById(carId);
        Car car = carById.getData();
        String tid = car.getTid();
        String trid = car.getTrid();

        // 调用地图去上传
        PointRequest pointRequest = new PointRequest();
        pointRequest.setTid(tid);
        pointRequest.setTrid(trid);
        pointRequest.setPoints(apiDriverPointRequest.getPoints());

        return serviceMapClient.upload(pointRequest);

    }
}
