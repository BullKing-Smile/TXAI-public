package com.txai.servicemap.service;

import com.txai.common.dto.ResponseResult;
import com.txai.common.request.PointRequest;
import com.txai.servicemap.remote.PointClient;
import org.springframework.stereotype.Service;

@Service
public class PointService {

    private final PointClient pointClient;

    public PointService(PointClient pointClient) {
        this.pointClient = pointClient;
    }

    public ResponseResult upload(PointRequest pointRequest) {

        return pointClient.upload(pointRequest);
    }
}
