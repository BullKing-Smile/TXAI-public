package com.txai.apidriver.controller;

import com.txai.apidriver.service.PointService;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.ApiDriverPointRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/point")
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    @PostMapping("/upload")
    public ResponseResult upload(@RequestBody ApiDriverPointRequest apiDriverPointRequest) {

        return pointService.upload(apiDriverPointRequest);
    }
}
