package com.txai.servicemap.service;

import com.txai.common.constant.CommonStatusEnum;
import com.txai.common.dto.DicDistrict;
import com.txai.common.dto.ResponseResult;
import com.txai.servicemap.mapper.DicDistrictMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DicDistrictService {
    private final DicDistrictMapper dicDistrictMapper;

    public DicDistrictService(DicDistrictMapper dicDistrictMapper) {
        this.dicDistrictMapper = dicDistrictMapper;
    }

    public ResponseResult<DicDistrict> getDicDistrictByCityCode(String addressCode) {

        log.info("address code ="+addressCode);
        Map<String, Object> map = new HashMap<>();
        map.put("address_code", addressCode);
        List<DicDistrict> dicDistricts = dicDistrictMapper.selectByMap(map);
        if (null != dicDistricts && dicDistricts.size() > 0) {
            return ResponseResult.success(dicDistricts.get(0));
        }
        return ResponseResult.fail(CommonStatusEnum.DIC_DISTRICT_NOT_EXISTS);
    }

}
