package com.txai.servicemap.controller;

import com.txai.common.dto.ResponseResult;
import com.txai.servicemap.service.ServiceMapService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceController {
    private final ServiceMapService serviceMapService;

    public ServiceController(ServiceMapService serviceMapService) {
        this.serviceMapService = serviceMapService;
    }

    /**
     * 创建服务
     *
     * @param name
     * @return
     */
    @PostMapping("/add")
    public ResponseResult add(String name) {

        return serviceMapService.add(name);
    }
}
