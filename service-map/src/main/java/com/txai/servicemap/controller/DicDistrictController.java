package com.txai.servicemap.controller;

import com.txai.common.dto.DicDistrict;
import com.txai.common.dto.ResponseResult;
import com.txai.servicemap.service.DicDistrictService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DicDistrictController {

    private final DicDistrictService dicDistrictService;
    public DicDistrictController(DicDistrictService dicDistrictService) {
        this.dicDistrictService = dicDistrictService;
    }

    // TODO: 2025/6/21
    // initDicDistrictData
    public ResponseResult initDicDistrictData() {
        return ResponseResult.success();
    }

    @GetMapping("/district/{addressCode}")
    public ResponseResult<DicDistrict> districe(@PathVariable("addressCode") String addressCode) {
        return dicDistrictService.getDicDistrictByCityCode(addressCode);
    }
}
