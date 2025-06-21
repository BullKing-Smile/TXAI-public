package com.txai.servicemap.controller;

import com.txai.common.dto.ResponseResult;
import com.txai.servicemap.service.TrackService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/track")
public class TrackController {

    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @PostMapping("/add")
    public ResponseResult add(String tid) {

        return trackService.add(tid);
    }
}
