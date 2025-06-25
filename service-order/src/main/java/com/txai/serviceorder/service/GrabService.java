package com.txai.serviceorder.service;

import com.txai.common.dto.ResponseResult;
import com.txai.common.request.DriverGrabRequest;

public interface GrabService {

    public ResponseResult grab(DriverGrabRequest driverGrabRequest) ;
}