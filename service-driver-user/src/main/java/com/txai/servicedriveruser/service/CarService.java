package com.txai.servicedriveruser.service;

import com.txai.common.constant.CommonStatusEnum;
import com.txai.common.dto.Car;
import com.txai.common.dto.ResponseResult;
import com.txai.common.response.TerminalResponse;
import com.txai.common.response.TrackResponse;
import com.txai.servicedriveruser.mapper.CarMapper;
import com.txai.servicedriveruser.remote.ServiceMapClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CarService {

    @Autowired
    private CarMapper carMapper;

    @Autowired
    private ServiceMapClient serviceMapClient;

    @Transactional
    public ResponseResult addCar(Car car){
        LocalDateTime now = LocalDateTime.now();
        car.setGmtModified(now);
        car.setGmtCreate(now);
        // 保存车辆
        carMapper.insert(car);

        // 获得此车辆的终端id：tid
        ResponseResult<TerminalResponse> responseResult = serviceMapClient.addTerminal(car.getVehicleNo(), car.getId()+"");
        String tid = responseResult.getData().getTid();
        car.setTid(tid);

        // 获得此车辆的轨迹id：trid
        ResponseResult<TrackResponse> trackResponseResponseResult = serviceMapClient.addTrack(tid);
        String trid = trackResponseResponseResult.getData().getTrid();
        String trname = trackResponseResponseResult.getData().getTrname();

        car.setTrid(trid);
        car.setTrname(trname);

        carMapper.updateById(car);


        return ResponseResult.success("");
    }

    public ResponseResult<Car> getCarById(Long id){
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);

        List<Car> cars = carMapper.selectByMap(map);
        if (null != cars && cars.size() > 0) {
            return ResponseResult.success(cars.get(0));
        }
        return ResponseResult.fail(CommonStatusEnum.CAR_NOT_EXISTS);
    }

}
