package com.txai.apidriver.controller;

import com.txai.apidriver.service.DriverCarBindingRelationshipService;
import com.txai.common.dto.DriverCarBindingRelationship;
import com.txai.common.dto.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/driver-car-binding-relationship")
public class DriverCarBindingRelationshipController {

    private final DriverCarBindingRelationshipService driverCarBindingRelationshipService;

    public DriverCarBindingRelationshipController(DriverCarBindingRelationshipService driverCarBindingRelationshipService) {
        this.driverCarBindingRelationshipService = driverCarBindingRelationshipService;
    }

    @PostMapping("/bind")
    public ResponseResult bind(@RequestBody DriverCarBindingRelationship driverCarBindingRelationship) {
        return driverCarBindingRelationshipService.bind(driverCarBindingRelationship);
    }

    @PostMapping("/unbind")
    public ResponseResult unbind(@RequestBody DriverCarBindingRelationship driverCarBindingRelationship) {
        return driverCarBindingRelationshipService.unbind(driverCarBindingRelationship);
    }
}
