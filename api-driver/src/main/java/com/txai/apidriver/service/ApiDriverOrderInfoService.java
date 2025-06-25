package com.txai.apidriver.service;

import com.txai.apidriver.remote.ServiceDriverUserClient;
import com.txai.apidriver.remote.ServiceOrderClient;
import com.txai.common.constant.CommonStatusEnum;
import com.txai.common.constant.DriverCarConstants;
import com.txai.common.constant.IdentityEnum;
import com.txai.common.dto.DriverCarBindingRelationship;
import com.txai.common.dto.DriverUserWorkStatus;
import com.txai.common.dto.OrderInfo;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.DriverGrabRequest;
import com.txai.common.request.OrderRequest;
import com.txai.common.response.OrderDriverResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;


@Service
public class ApiDriverOrderInfoService {

    private final ServiceOrderClient serviceOrderClient;
    private final ServiceDriverUserClient serviceDriverUserClient;

    public ApiDriverOrderInfoService(ServiceOrderClient serviceOrderClient,
                                     ServiceDriverUserClient serviceDriverUserClient) {
        this.serviceOrderClient = serviceOrderClient;
        this.serviceDriverUserClient = serviceDriverUserClient;
    }

    public ResponseResult toPickUpPassenger(OrderRequest orderRequest) {
        return serviceOrderClient.toPickUpPassenger(orderRequest);
    }

    public ResponseResult arrivedDeparture(OrderRequest orderRequest) {
        return serviceOrderClient.arrivedDeparture(orderRequest);
    }

    /**
     * 司机接到乘客
     *
     * @param orderRequest
     * @return
     */
    public ResponseResult pickUpPassenger(@RequestBody OrderRequest orderRequest) {
        return serviceOrderClient.pickUpPassenger(orderRequest);
    }

    public ResponseResult passengerGetoff(OrderRequest orderRequest) {
        return serviceOrderClient.passengerGetoff(orderRequest);
    }

    public ResponseResult cancel(Long orderId) {
        return serviceOrderClient.cancel(orderId, IdentityEnum.Driver.getId());
    }

    public ResponseResult<OrderInfo> detail(Long orderId) {
        return serviceOrderClient.detail(orderId);
    }


    public ResponseResult<OrderInfo> currentOrder(String phone, String identity) {
        return serviceOrderClient.current(phone, identity);
    }

    /**
     * 司机抢单
     *
     * @param driverPhone
     * @param orderId
     * @return
     */
    public ResponseResult grap(String driverPhone, Long orderId, String receiveOrderCarLongitude, String receiveOrderCarLatitude) {

        // 根据 司机的电话，查询车辆信息
        ResponseResult<DriverCarBindingRelationship> driverCarRelationShipResponseResult = serviceDriverUserClient.getDriverCarRelationShip(driverPhone);

        if (driverCarRelationShipResponseResult == null) {
            return ResponseResult.fail(CommonStatusEnum.DRIVER_CAR_BIND_NOT_EXISTS);
        }
        DriverCarBindingRelationship driverCarBindingRelationship = driverCarRelationShipResponseResult.getData();
        Long carId = driverCarBindingRelationship.getCarId();

        ResponseResult<OrderDriverResponse> availableDriverResponseResult = serviceDriverUserClient.getAvailableDriver(carId);
        if (availableDriverResponseResult == null || availableDriverResponseResult.getData() == null) {
            return ResponseResult.fail(CommonStatusEnum.CAR_NOT_EXISTS.getCode(), CommonStatusEnum.CAR_NOT_EXISTS.getValue());
        }

        OrderDriverResponse orderDriverResponse = availableDriverResponseResult.getData();
        Long driverId = orderDriverResponse.getDriverId();
        String licenseId = orderDriverResponse.getLicenseId();
        String vehicleNo = orderDriverResponse.getVehicleNo();
        String vehicleType = orderDriverResponse.getVehicleType();

//        orderInfo.setOrderStatus(OrderConstants.DRIVER_RECEIVE_ORDER);
        // 执行抢单动作
        DriverGrabRequest driverGrabRequest = new DriverGrabRequest();
        driverGrabRequest.setOrderId(orderId);
        driverGrabRequest.setDriverId(driverId);
        driverGrabRequest.setCarId(carId);
        driverGrabRequest.setDriverPhone(driverPhone);
        driverGrabRequest.setLicenseId(licenseId);
        driverGrabRequest.setVehicleNo(vehicleNo);
        driverGrabRequest.setVehicleType(vehicleType);
        driverGrabRequest.setReceiveOrderCarLatitude(receiveOrderCarLatitude);
        driverGrabRequest.setReceiveOrderCarLongitude(receiveOrderCarLongitude);

        ResponseResult responseResult = serviceOrderClient.driverGrab(driverGrabRequest);
        if (responseResult.getCode() != CommonStatusEnum.SUCCESS.getCode()) {
            return ResponseResult.fail(CommonStatusEnum.ORDER_UPDATE_ERROR);
        }
        // 修改司机的工作状态

        DriverUserWorkStatus driverUserWorkStatus = new DriverUserWorkStatus();
        driverUserWorkStatus.setDriverId(driverId);
        driverUserWorkStatus.setWorkStatus(DriverCarConstants.DRIVER_WORK_STATUS_SERVING);

        responseResult = serviceDriverUserClient.changeWorkStatus(driverUserWorkStatus);
        if (responseResult.getCode() != CommonStatusEnum.SUCCESS.getCode()) {
            return ResponseResult.fail(CommonStatusEnum.DRIVER_STATUS_UPDATE_ERROR);
        }

        return ResponseResult.success();
    }

}
