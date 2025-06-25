package com.txai.serviceorder.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.txai.common.constant.CommonStatusEnum;
import com.txai.common.constant.IdentityEnum;
import com.txai.common.constant.OrderCancelEnum;
import com.txai.common.constant.OrderStatusEnum;
import com.txai.common.dto.Car;
import com.txai.common.dto.OrderInfo;
import com.txai.common.dto.PriceRule;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.DriverGrabRequest;
import com.txai.common.request.OrderRequest;
import com.txai.common.request.PriceRuleIsNewRequest;
import com.txai.common.request.PushRequest;
import com.txai.common.response.OrderDriverResponse;
import com.txai.common.response.TerminalResponse;
import com.txai.common.response.TrsearchResponse;
import com.txai.common.util.RedisPrefixUtils;
import com.txai.serviceorder.entity.DriverOrderStatistics;
import com.txai.serviceorder.mapper.DriverOrderStatisticsMapper;
import com.txai.serviceorder.mapper.OrderInfoMapper;
import com.txai.serviceorder.remote.ServiceDriverUserClient;
import com.txai.serviceorder.remote.ServiceMapClient;
import com.txai.serviceorder.remote.ServicePriceClient;
import com.txai.serviceorder.remote.ServiceSsePushClient;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OrderInfoService {

    private final OrderInfoMapper orderInfoMapper;

    private final ServicePriceClient servicePriceClient;

    private final ServiceDriverUserClient serviceDriverUserClient;

    private final StringRedisTemplate stringRedisTemplate;

    private final ServiceMapClient serviceMapClient;

    private final RedissonClient redissonClient;

    private final ServiceSsePushClient serviceSsePushClient;

    private final DriverOrderStatisticsMapper driverOrderStatisticsMapper;

    public OrderInfoService(OrderInfoMapper orderInfoMapper,
                            ServicePriceClient servicePriceClient,
                            ServiceDriverUserClient serviceDriverUserClient,
                            ServiceMapClient serviceMapClient,
                            StringRedisTemplate stringRedisTemplate,
                            RedissonClient redissonClient,
                            ServiceSsePushClient serviceSsePushClient,
                            DriverOrderStatisticsMapper driverOrderStatisticsMapper) {
        this.orderInfoMapper = orderInfoMapper;
        this.servicePriceClient = servicePriceClient;
        this.serviceMapClient = serviceMapClient;
        this.serviceDriverUserClient = serviceDriverUserClient;
        this.stringRedisTemplate = stringRedisTemplate;
        this.redissonClient = redissonClient;
        this.serviceSsePushClient = serviceSsePushClient;
        this.driverOrderStatisticsMapper = driverOrderStatisticsMapper;
    }

    /**
     * 新建订单
     * 拒绝下单的情况
     * 1. 有正在派单的订单
     * 2. 有未支付的订单
     * 3. 在黑名单中的用户
     *
     * @param orderRequest
     * @return
     */
    public ResponseResult add(OrderRequest orderRequest) {

        // 测试当前城市是否有可用的司机
        ResponseResult<Boolean> availableDriver = serviceDriverUserClient.isAvailableDriver(orderRequest.getAddress());
        log.info("测试城市是否有司机结果：" + availableDriver.getData());
        if (!availableDriver.getData()) {
            return ResponseResult.fail(CommonStatusEnum.AVAILABLE_DRIVER_EMPTY);
        }

        // 需要判断计价规则的版本是否为最新
        PriceRuleIsNewRequest priceRuleIsNewRequest = new PriceRuleIsNewRequest();
        priceRuleIsNewRequest.setFareType(orderRequest.getFareType());
        priceRuleIsNewRequest.setFareVersion(orderRequest.getFareVersion());
        ResponseResult<Boolean> aNew = servicePriceClient.isNew(priceRuleIsNewRequest);
        if (!(aNew.getData())) {
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_CHANGED);
        }

        // 需要判断 下单的设备是否是 黑名单设备
//        if (isBlackDevice(orderRequest)) {
//            return ResponseResult.fail(CommonStatusEnum.DEVICE_IS_BLACK);
//        }

        // 判断：下单的城市和计价规则是否正常
        if (!isPriceRuleExists(orderRequest)) {
            return ResponseResult.fail(CommonStatusEnum.CITY_SERVICE_NOT_SERVICE);
        }


        // 判断乘客 是否有进行中的订单
        if (isPassengerOrderGoingon(orderRequest.getPassengerId()) > 0) {
            return ResponseResult.fail(CommonStatusEnum.ORDER_GOING_ON);
        }

        // 创建订单
        OrderInfo orderInfo = new OrderInfo();

        BeanUtils.copyProperties(orderRequest, orderInfo);

        orderInfo.setOrderStatus(OrderStatusEnum.START.getCode());

        LocalDateTime now = LocalDateTime.now();
        orderInfo.setGmtCreate(now);
        orderInfo.setGmtModified(now);

        orderInfoMapper.insert(orderInfo);

        // 定时任务的处理
        for (int i = 0; i < 6; i++) {
            // 派单 dispatchRealTimeOrder
            int result = dispatchRealTimeOrder(orderInfo);
            if (result == 1) {
                break;
            }
            if (i == 5) {
                // 订单无效
                orderInfo.setOrderStatus(OrderStatusEnum.INVALID.getCode());
                orderInfoMapper.updateById(orderInfo);
            } else {
                // 等待20s
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }

        return ResponseResult.success();
    }

    public ResponseResult book(OrderRequest orderRequest) {

        // 测试当前城市是否有可用的司机
        ResponseResult<Boolean> availableDriver = serviceDriverUserClient.isAvailableDriver(orderRequest.getAddress());
        log.info("测试城市是否有司机结果：" + availableDriver.getData());
        if (!availableDriver.getData()) {
            return ResponseResult.fail(CommonStatusEnum.AVAILABLE_DRIVER_EMPTY);
        }

        // 需要判断计价规则的版本是否为最新
        PriceRuleIsNewRequest priceRuleIsNewRequest = new PriceRuleIsNewRequest();
        priceRuleIsNewRequest.setFareType(orderRequest.getFareType());
        priceRuleIsNewRequest.setFareVersion(orderRequest.getFareVersion());
        ResponseResult<Boolean> aNew = servicePriceClient.isNew(priceRuleIsNewRequest);
        if (!(aNew.getData())) {
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_CHANGED);
        }

        // 需要判断 下单的设备是否是 黑名单设备
//        if (isBlackDevice(orderRequest)) {
//            return ResponseResult.fail(CommonStatusEnum.DEVICE_IS_BLACK);
//        }

        // 判断：下单的城市和计价规则是否正常
        if (!isPriceRuleExists(orderRequest)) {
            return ResponseResult.fail(CommonStatusEnum.CITY_SERVICE_NOT_SERVICE);
        }


        // 判断乘客 是否有进行中的订单
        if (isPassengerOrderGoingon(orderRequest.getPassengerId()) > 0) {
            return ResponseResult.fail(CommonStatusEnum.ORDER_GOING_ON);
        }

        // 创建订单
        OrderInfo orderInfo = new OrderInfo();

        BeanUtils.copyProperties(orderRequest, orderInfo);

        orderInfo.setOrderStatus(OrderStatusEnum.START.getCode());

        LocalDateTime now = LocalDateTime.now();
        orderInfo.setGmtCreate(now);
        orderInfo.setGmtModified(now);

        orderInfoMapper.insert(orderInfo);

        // 定时任务的处理
        for (int i = 0; i < 6; i++) {
            // 派单 dispatchRealTimeOrder
            int result = dispatchBookOrder(orderInfo);
            if (result == 1) {
                break;
            }
            // 等待20s
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return ResponseResult.success();
    }

    /**
     * 实时订单派单逻辑
     * 如果返回1：派单成功
     *
     * @param orderInfo
     */
    public int dispatchRealTimeOrder(OrderInfo orderInfo) {
        log.info("循环一次");
        int result = 0;

        //2km
        String depLatitude = orderInfo.getDepLatitude();
        String depLongitude = orderInfo.getDepLongitude();

        String center = depLatitude + "," + depLongitude;

        List<Integer> radiusList = new ArrayList<>();
        radiusList.add(2000);
        radiusList.add(4000);
        radiusList.add(5000);
        // 搜索结果
        ResponseResult<List<TerminalResponse>> listResponseResult = null;
        // goto是为了测试。
        radius:
        for (int i = 0; i < radiusList.size(); i++) {
            Integer radius = radiusList.get(i);
            listResponseResult = serviceMapClient.terminalAroundSearch(center, radius);

            log.info("在半径为" + radius + "的范围内，寻找车辆,结果：" + (new JSONArray(listResponseResult.getData())));

            // 获得终端  [{"carId":1578641048288702465,"tid":"584169988"}]

            // 解析终端
            List<TerminalResponse> data = listResponseResult.getData();

            // 为了测试是否从地图上获取到司机
//            List<TerminalResponse> data = new ArrayList<>();
            for (int j = 0; j < data.size(); j++) {
                TerminalResponse terminalResponse = data.get(j);
                Long carId = terminalResponse.getCarId();

                String longitude = terminalResponse.getLongitude();
                String latitude = terminalResponse.getLatitude();

                // 查询是否有对于的可派单司机
                ResponseResult<OrderDriverResponse> availableDriver = serviceDriverUserClient.getAvailableDriver(carId);
                if (availableDriver.getCode() == CommonStatusEnum.AVAILABLE_DRIVER_EMPTY.getCode()) {
                    log.info("没有车辆ID：" + carId + ",对于的司机");
                    continue;
                } else {
                    log.info("车辆ID：" + carId + "找到了正在出车的司机");

                    OrderDriverResponse orderDriverResponse = availableDriver.getData();
                    Long driverId = orderDriverResponse.getDriverId();
                    String driverPhone = orderDriverResponse.getDriverPhone();
                    String licenseId = orderDriverResponse.getLicenseId();
                    String vehicleNo = orderDriverResponse.getVehicleNo();
                    String vehicleTypeFromCar = orderDriverResponse.getVehicleType();

                    // 判断车辆的车型是否符合？
                    String vehicleType = orderInfo.getVehicleType();
                    if (!vehicleType.trim().equals(vehicleTypeFromCar.trim())) {
                        System.out.println("车型不符合");
                        continue;
                    }


                    String lockKey = (driverId + "").intern();
                    RLock lock = redissonClient.getLock(lockKey);
                    lock.lock();

                    // 判断司机 是否有进行中的订单
                    if (isDriverOrderGoingon(driverId) > 0) {
                        lock.unlock();
                        continue;
                    }
                    // 订单直接匹配司机
                    // 查询当前车辆信息
                    QueryWrapper<Car> carQueryWrapper = new QueryWrapper<>();
                    carQueryWrapper.eq("id", carId);


                    // 设置订单中和司机车辆相关的信息
                    orderInfo.setDriverId(driverId);
                    orderInfo.setDriverPhone(driverPhone);
                    orderInfo.setCarId(carId);
                    // 从地图中来
                    orderInfo.setReceiveOrderCarLongitude(longitude);
                    orderInfo.setReceiveOrderCarLatitude(latitude);

                    orderInfo.setReceiveOrderTime(LocalDateTime.now());
                    orderInfo.setLicenseId(licenseId);
                    orderInfo.setVehicleNo(vehicleNo);
                    orderInfo.setOrderStatus(OrderStatusEnum.DRIVER_RECEIVE_ORDER.getCode());

                    orderInfoMapper.updateById(orderInfo);

                    // 通知司机
                    JSONObject driverContent = new JSONObject();

                    driverContent.put("orderId", orderInfo.getId());
                    driverContent.put("passengerId", orderInfo.getPassengerId());
                    driverContent.put("passengerPhone", orderInfo.getPassengerPhone());
                    driverContent.put("departure", orderInfo.getDeparture());
                    driverContent.put("depLongitude", orderInfo.getDepLongitude());
                    driverContent.put("depLatitude", orderInfo.getDepLatitude());

                    driverContent.put("destination", orderInfo.getDestination());
                    driverContent.put("destLongitude", orderInfo.getDestLongitude());
                    driverContent.put("destLatitude", orderInfo.getDestLatitude());

                    PushRequest pushRequest = new PushRequest();
                    pushRequest.setUserId(driverId);
                    pushRequest.setIdentity(IdentityEnum.Driver.getId());
                    pushRequest.setContent(driverContent.toString());

                    log.info("推送通知司机");
                    serviceSsePushClient.push(pushRequest);

                    // 通知乘客
                    JSONObject passengerContent = new JSONObject();
                    passengerContent.put("orderId", orderInfo.getId());
                    passengerContent.put("driverId", orderInfo.getDriverId());
                    passengerContent.put("driverPhone", orderInfo.getDriverPhone());
                    passengerContent.put("vehicleNo", orderInfo.getVehicleNo());
                    // 车辆信息，调用车辆服务
                    ResponseResult<Car> carById = serviceDriverUserClient.getCarById(carId);
                    Car carRemote = carById.getData();

                    passengerContent.put("brand", carRemote.getBrand());
                    passengerContent.put("model", carRemote.getModel());
                    passengerContent.put("vehicleColor", carRemote.getVehicleColor());

                    passengerContent.put("receiveOrderCarLongitude", orderInfo.getReceiveOrderCarLongitude());
                    passengerContent.put("receiveOrderCarLatitude", orderInfo.getReceiveOrderCarLatitude());

                    PushRequest pushRequest1 = new PushRequest();
                    pushRequest1.setUserId(orderInfo.getPassengerId());
                    pushRequest1.setIdentity(IdentityEnum.Passenger.getId());
                    pushRequest1.setContent(passengerContent.toString());

                    log.info("通知乘客端");
                    serviceSsePushClient.push(pushRequest1);
                    result = 1;
                    lock.unlock();

                    // 退出，不在进行 司机的查找.如果派单成功，则退出循环
                    break radius;
                }

            }

        }

        return result;

    }

    public int dispatchBookOrder(OrderInfo orderInfo) {
        log.info("循环一次");
        int result = 0;

        //2km
        String depLatitude = orderInfo.getDepLatitude();
        String depLongitude = orderInfo.getDepLongitude();

        String center = depLatitude + "," + depLongitude;

        List<Integer> radiusList = new ArrayList<>();
        radiusList.add(2000);
        radiusList.add(4000);
        radiusList.add(5000);
        // 搜索结果
        ResponseResult<List<TerminalResponse>> listResponseResult = null;
        // goto是为了测试。
        radius:
        for (int i = 0; i < radiusList.size(); i++) {
            Integer radius = radiusList.get(i);
            listResponseResult = serviceMapClient.terminalAroundSearch(center, radius);

            log.info("在半径为" + radius + "的范围内，寻找车辆,结果：" + (new JSONArray(listResponseResult.getData())));

            // 获得终端  [{"carId":1578641048288702465,"tid":"584169988"}]

            // 解析终端
            List<TerminalResponse> data = listResponseResult.getData();

            // 为了测试是否从地图上获取到司机
//            List<TerminalResponse> data = new ArrayList<>();
            for (int j = 0; j < data.size(); j++) {
                TerminalResponse terminalResponse = data.get(j);
                Long carId = terminalResponse.getCarId();

                String longitude = terminalResponse.getLongitude();
                String latitude = terminalResponse.getLatitude();

                // 查询是否有对于的可派单司机
                ResponseResult<OrderDriverResponse> availableDriver = serviceDriverUserClient.getAvailableDriver(carId);
                if (availableDriver.getCode() == CommonStatusEnum.AVAILABLE_DRIVER_EMPTY.getCode()) {
                    log.info("没有车辆ID：" + carId + ",对于的司机");
                    continue;
                } else {
                    log.info("车辆ID：" + carId + "找到了正在出车的司机");

                    OrderDriverResponse orderDriverResponse = availableDriver.getData();
                    Long driverId = orderDriverResponse.getDriverId();
                    String driverPhone = orderDriverResponse.getDriverPhone();
                    String licenseId = orderDriverResponse.getLicenseId();
                    String vehicleNo = orderDriverResponse.getVehicleNo();
                    String vehicleTypeFromCar = orderDriverResponse.getVehicleType();

                    // 判断车辆的车型是否符合？
                    String vehicleType = orderInfo.getVehicleType();
                    if (!vehicleType.trim().equals(vehicleTypeFromCar.trim())) {
                        System.out.println("车型不符合");
                        continue;
                    }

                    // 通知司机
                    JSONObject driverContent = new JSONObject();

                    driverContent.put("orderId", orderInfo.getId());
                    driverContent.put("passengerId", orderInfo.getPassengerId());
                    driverContent.put("passengerPhone", orderInfo.getPassengerPhone());
                    driverContent.put("departure", orderInfo.getDeparture());
                    driverContent.put("depLongitude", orderInfo.getDepLongitude());
                    driverContent.put("depLatitude", orderInfo.getDepLatitude());

                    driverContent.put("destination", orderInfo.getDestination());
                    driverContent.put("destLongitude", orderInfo.getDestLongitude());
                    driverContent.put("destLatitude", orderInfo.getDestLatitude());

                    PushRequest pushRequest = new PushRequest();
                    pushRequest.setUserId(driverId);
                    pushRequest.setIdentity(IdentityEnum.Driver.getId());
                    pushRequest.setContent(driverContent.toString());

                    log.info("推送通知司机");
                    serviceSsePushClient.push(pushRequest);


                    result = 1;
                    // 退出，不在进行 司机的查找.如果派单成功，则退出循环
                    break radius;
                }

            }

        }

        return result;

    }

    /**
     * 计价规则是否存在
     *
     * @param orderRequest
     * @return
     */
    private boolean isPriceRuleExists(OrderRequest orderRequest) {
        String fareType = orderRequest.getFareType();
        int index = fareType.indexOf("$");
        String cityCode = fareType.substring(0, index);
        String vehicleType = fareType.substring(index + 1);

        PriceRule priceRule = new PriceRule();
        priceRule.setCityCode(cityCode);
        priceRule.setVehicleType(vehicleType);

        ResponseResult<Boolean> booleanResponseResult = servicePriceClient.ifPriceExists(priceRule);
        return booleanResponseResult.getData();

    }

    /**
     * 是否是黑名单
     *
     * @param orderRequest
     * @return
     */
    private boolean isBlackDevice(OrderRequest orderRequest) {
        String deviceCode = orderRequest.getDeviceCode();
        // 生成key
        String deviceCodeKey = RedisPrefixUtils.blackDeviceCodePrefix + deviceCode;
        Boolean aBoolean = stringRedisTemplate.hasKey(deviceCodeKey);
        if (aBoolean) {
            String s = stringRedisTemplate.opsForValue().get(deviceCodeKey);
            int i = Integer.parseInt(s);
            if (i >= 2) {
                // 当前设备超过下单次数
                return true;
            } else {
                stringRedisTemplate.opsForValue().increment(deviceCodeKey);
            }

        } else {
            stringRedisTemplate.opsForValue().setIfAbsent(deviceCodeKey, "1", 1L, TimeUnit.HOURS);
        }
        return false;
    }

    /**
     * 判断是否有 业务中的订单
     *
     * @param passengerId
     * @return
     */
    private long isPassengerOrderGoingon(Long passengerId) {
        // 判断有正在进行的订单不允许下单
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("passenger_id", passengerId);
        queryWrapper.and(wrapper -> wrapper.eq("order_status", OrderStatusEnum.START.getCode()) //ORDER_START
                .or().eq("order_status", OrderStatusEnum.DRIVER_RECEIVE_ORDER.getCode()) // DRIVER_RECEIVE_ORDER
                .or().eq("order_status", OrderStatusEnum.DRIVER_TO_PICKUP.getCode())
                .or().eq("order_status", OrderStatusEnum.DRIVER_ARRIVED_DEPARTURE.getCode())
                .or().eq("order_status", OrderStatusEnum.PASSENGER_GETON.getCode())
                .or().eq("order_status", OrderStatusEnum.PASSENGER_GETOFF.getCode())
                .or().eq("order_status", OrderStatusEnum.UNPAID.getCode())
        );


        Long validOrderNumber = orderInfoMapper.selectCount(queryWrapper);

        return validOrderNumber;

    }

    /**
     * 判断是否有 业务中的订单
     *
     * @param driverId
     * @return
     */
    private long isDriverOrderGoingon(Long driverId) {
        // 判断有正在进行的订单不允许下单
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("driver_id", driverId);
        queryWrapper.and(wrapper -> wrapper
                .eq("order_status", OrderStatusEnum.DRIVER_RECEIVE_ORDER.getCode())
                .or().eq("order_status", OrderStatusEnum.DRIVER_TO_PICKUP.getCode())
                .or().eq("order_status", OrderStatusEnum.DRIVER_ARRIVED_DEPARTURE.getCode())
                .or().eq("order_status", OrderStatusEnum.PASSENGER_GETON.getCode())

        );


        Long validOrderNumber = orderInfoMapper.selectCount(queryWrapper);
        log.info("司机Id：" + driverId + ",正在进行的订单的数量：" + validOrderNumber);

        return validOrderNumber;

    }

    /**
     * 去接乘客
     *
     * @param orderRequest
     * @return
     */
    public ResponseResult toPickUpPassenger(OrderRequest orderRequest) {
        Long orderId = orderRequest.getOrderId();
        LocalDateTime toPickUpPassengerTime = orderRequest.getToPickUpPassengerTime();
        String toPickUpPassengerLongitude = orderRequest.getToPickUpPassengerLongitude();
        String toPickUpPassengerLatitude = orderRequest.getToPickUpPassengerLatitude();
        String toPickUpPassengerAddress = orderRequest.getToPickUpPassengerAddress();
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", orderId);
        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);

        orderInfo.setToPickUpPassengerAddress(toPickUpPassengerAddress);
        orderInfo.setToPickUpPassengerLatitude(toPickUpPassengerLatitude);
        orderInfo.setToPickUpPassengerLongitude(toPickUpPassengerLongitude);
        orderInfo.setToPickUpPassengerTime(LocalDateTime.now());
        orderInfo.setOrderStatus(OrderStatusEnum.DRIVER_TO_PICKUP.getCode());

        orderInfoMapper.updateById(orderInfo);

        return ResponseResult.success();

    }

    /**
     * 司机到达乘客上车点
     *
     * @param orderRequest
     * @return
     */
    public ResponseResult arrivedDeparture(OrderRequest orderRequest) {
        Long orderId = orderRequest.getOrderId();

        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", orderId);

        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);
        orderInfo.setOrderStatus(OrderStatusEnum.DRIVER_ARRIVED_DEPARTURE.getCode());

        orderInfo.setDriverArrivedDepartureTime(LocalDateTime.now());
        orderInfoMapper.updateById(orderInfo);
        return ResponseResult.success();
    }

    /**
     * 司机接到乘客
     *
     * @param orderRequest
     * @return
     */
    public ResponseResult pickUpPassenger(@RequestBody OrderRequest orderRequest) {
        Long orderId = orderRequest.getOrderId();

        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", orderId);
        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);

        orderInfo.setPickUpPassengerLongitude(orderRequest.getPickUpPassengerLongitude());
        orderInfo.setPickUpPassengerLatitude(orderRequest.getPickUpPassengerLatitude());
        orderInfo.setPickUpPassengerTime(LocalDateTime.now());
        orderInfo.setOrderStatus(OrderStatusEnum.PASSENGER_GETON.getCode());

        orderInfoMapper.updateById(orderInfo);
        return ResponseResult.success();
    }

    /**
     * 乘客下车到达目的地，行程终止
     *
     * @param orderRequest
     * @return
     */
    public ResponseResult passengerGetoff(@RequestBody OrderRequest orderRequest) {
        Long orderId = orderRequest.getOrderId();

        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", orderId);
        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);

        orderInfo.setPassengerGetoffTime(LocalDateTime.now());
        orderInfo.setPassengerGetoffLongitude(orderRequest.getPassengerGetoffLongitude());
        orderInfo.setPassengerGetoffLatitude(orderRequest.getPassengerGetoffLatitude());

        orderInfo.setOrderStatus(OrderStatusEnum.PASSENGER_GETOFF.getCode());
        // 订单行驶的路程和时间,调用 service-map
        ResponseResult<Car> carById = serviceDriverUserClient.getCarById(orderInfo.getCarId());
        Long starttime = orderInfo.getPickUpPassengerTime().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        Long endtime = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        System.out.println("开始时间：" + starttime);
        System.out.println("结束时间：" + endtime);
        // 1668078028000l,测试的时候不要跨天
        ResponseResult<TrsearchResponse> trsearch = serviceMapClient.trsearch(carById.getData().getTid(), starttime, endtime);
        TrsearchResponse data = trsearch.getData();
        Long driveMile = data.getDriveMile();
        Long driveTime = data.getDriveTime();

        orderInfo.setDriveMile(driveMile);
        orderInfo.setDriveTime(driveTime);

        // 获取价格
        String address = orderInfo.getAddress();
        String vehicleType = orderInfo.getVehicleType();
        ResponseResult<Double> doubleResponseResult = servicePriceClient.calculatePrice(driveMile.intValue(), driveTime.intValue(), address, vehicleType);
        Double price = doubleResponseResult.getData();
        orderInfo.setPrice(price);

        orderInfoMapper.updateById(orderInfo);
        return ResponseResult.success();
    }

    /**
     * 支付
     *
     * @param orderRequest
     * @return
     */
    public ResponseResult pay(OrderRequest orderRequest) {

        Long orderId = orderRequest.getOrderId();
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);

        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getCode());
        orderInfoMapper.updateById(orderInfo);
        return ResponseResult.success();
    }

    /**
     * 订单取消
     *
     * @param orderId  订单Id
     * @param identity 身份：1：乘客，2：司机
     * @return
     */
    public ResponseResult cancel(Long orderId, String identity) {
        // 查询订单当前状态
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        Integer orderStatus = orderInfo.getOrderStatus();

        LocalDateTime cancelTime = LocalDateTime.now();
        Integer cancelOperator = null;
        Integer cancelTypeCode = null;

        // 正常取消
        int cancelType = 1;

        // 更新订单的取消状态
        // 如果是乘客取消
        if (identity.trim().equals(IdentityEnum.Passenger.getId())) {
            switch (OrderStatusEnum.get(orderStatus)) {
                // 订单开始
                case START:
                    cancelTypeCode = OrderCancelEnum.CANCEL_PASSENGER_BEFORE.getCode();
                    break;
                // 司机接到订单
                case DRIVER_RECEIVE_ORDER:
                    LocalDateTime receiveOrderTime = orderInfo.getReceiveOrderTime();
                    long between = ChronoUnit.MINUTES.between(receiveOrderTime, cancelTime);
                    if (between > 1) {
                        cancelTypeCode = OrderCancelEnum.CANCEL_PASSENGER_ILLEGAL.getCode();
                    } else {
                        cancelTypeCode = OrderCancelEnum.CANCEL_PASSENGER_BEFORE.getCode();
                    }
                    break;
                // 司机去接乘客
                case DRIVER_TO_PICKUP:
                    // 司机到达乘客起点
                case DRIVER_ARRIVED_DEPARTURE:
                    cancelTypeCode = OrderCancelEnum.CANCEL_PASSENGER_ILLEGAL.getCode();
                    break;
                default:
                    log.info("乘客取消失败");
                    cancelType = 0;
                    break;
            }
        }

        // 如果是司机取消
        if (identity.trim().equals(IdentityEnum.Driver.getId())) {
            switch (OrderStatusEnum.get(orderStatus)) {
                // 订单开始
                // 司机接到乘客
                case DRIVER_RECEIVE_ORDER:
                case DRIVER_TO_PICKUP:
                case DRIVER_ARRIVED_DEPARTURE:
                    LocalDateTime receiveOrderTime = orderInfo.getReceiveOrderTime();
                    long between = ChronoUnit.MINUTES.between(receiveOrderTime, cancelTime);
                    if (between > 1) {
                        cancelTypeCode = OrderCancelEnum.CANCEL_DRIVER_ILLEGAL.getCode();
                    } else {
                        cancelTypeCode = OrderCancelEnum.CANCEL_DRIVER_BEFORE.getCode();
                    }
                    break;

                default:
                    log.info("司机取消失败");
                    cancelType = 0;
                    break;
            }
        }


        if (cancelType == 0) {
            return ResponseResult.fail(CommonStatusEnum.ORDER_CANCEL_ERROR);
        }

        orderInfo.setCancelTypeCode(cancelTypeCode);
        orderInfo.setCancelTime(cancelTime);
        orderInfo.setCancelOperator(Integer.parseInt(identity));
        orderInfo.setOrderStatus(OrderStatusEnum.CANCELLED.getCode());

        orderInfoMapper.updateById(orderInfo);
        return ResponseResult.success();
    }

    public ResponseResult pushPayInfo(OrderRequest orderRequest) {

        Long orderId = orderRequest.getOrderId();

        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
        orderInfoMapper.updateById(orderInfo);
        return ResponseResult.success();

    }

    public ResponseResult<OrderInfo> detail(Long orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        return ResponseResult.success(orderInfo);
    }


    public ResponseResult<OrderInfo> current(String phone, String identity) {
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();

        if (identity.equals(IdentityEnum.Driver.getId())) {
            queryWrapper.eq("driver_phone", phone);

            queryWrapper.and(wrapper -> wrapper
                    .eq("order_status", OrderStatusEnum.DRIVER_RECEIVE_ORDER)
                    .or().eq("order_status", OrderStatusEnum.DRIVER_TO_PICKUP)
                    .or().eq("order_status", OrderStatusEnum.DRIVER_ARRIVED_DEPARTURE)
                    .or().eq("order_status", OrderStatusEnum.PASSENGER_GETON)

            );
        }
        if (identity.equals(IdentityEnum.Passenger.getId())) {
            queryWrapper.eq("passenger_phone", phone);
            queryWrapper.and(wrapper -> wrapper.eq("order_status", OrderStatusEnum.START.getCode())
                    .or().eq("order_status", OrderStatusEnum.DRIVER_RECEIVE_ORDER)
                    .or().eq("order_status", OrderStatusEnum.DRIVER_TO_PICKUP)
                    .or().eq("order_status", OrderStatusEnum.DRIVER_ARRIVED_DEPARTURE)
                    .or().eq("order_status", OrderStatusEnum.PASSENGER_GETON)
                    .or().eq("order_status", OrderStatusEnum.PASSENGER_GETOFF)
                    .or().eq("order_status", OrderStatusEnum.UNPAID)
            );
        }

        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);
        return ResponseResult.success(orderInfo);
    }

    /**
     * 司机抢单-基础代码
     *
     * @param driverGrabRequest
     * @return
     */
    @Transactional
    public ResponseResult grab(DriverGrabRequest driverGrabRequest) {

        System.out.println("请求来了：" + driverGrabRequest.getDriverId());
        Long orderId = driverGrabRequest.getOrderId();

        String orderIdStr = (orderId + "").intern();

        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if (orderInfo == null) {
            return ResponseResult.fail(CommonStatusEnum.ORDER_NOT_EXISTS);
        }

        int orderStatus = orderInfo.getOrderStatus();
        if (orderStatus != OrderStatusEnum.START.getCode()) {
            return ResponseResult.fail(CommonStatusEnum.ORDER_CAN_NOT_GRAB);
        }
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Long driverId = driverGrabRequest.getDriverId();
        Long carId = driverGrabRequest.getCarId();
        String licenseId = driverGrabRequest.getLicenseId();
        String vehicleNo = driverGrabRequest.getVehicleNo();
        String receiveOrderCarLatitude = driverGrabRequest.getReceiveOrderCarLatitude();
        String receiveOrderCarLongitude = driverGrabRequest.getReceiveOrderCarLongitude();
        String vehicleType = driverGrabRequest.getVehicleType();
        String driverPhone = driverGrabRequest.getDriverPhone();

        orderInfo.setDriverId(driverId);
        orderInfo.setDriverPhone(driverPhone);
        orderInfo.setCarId(carId);

        orderInfo.setReceiveOrderCarLongitude(receiveOrderCarLongitude);
        orderInfo.setReceiveOrderCarLatitude(receiveOrderCarLatitude);
        orderInfo.setReceiveOrderTime(LocalDateTime.now());

        orderInfo.setLicenseId(licenseId);
        orderInfo.setVehicleNo(vehicleNo);

        orderInfo.setVehicleType(vehicleType);

        orderInfo.setOrderStatus(OrderStatusEnum.DRIVER_RECEIVE_ORDER.getCode());

        orderInfoMapper.updateById(orderInfo);

//        int i = 1/0;

        // 添加司机当天抢单成功的数量
        // 先查询当天的数据
        QueryWrapper<DriverOrderStatistics> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("grab_order_date", LocalDate.now());
        queryWrapper.eq("driver_id", driverId);
        DriverOrderStatistics driverOrderStatistics = driverOrderStatisticsMapper.selectOne(queryWrapper);
        if (driverOrderStatistics == null) {
            driverOrderStatistics = new DriverOrderStatistics();
            driverOrderStatistics.setGrabOrderDate(LocalDate.now());
            driverOrderStatistics.setGrabOrderSuccessCount(1);
            driverOrderStatistics.setDriverId(driverId);
            driverOrderStatisticsMapper.insert(driverOrderStatistics);
        } else {
            driverOrderStatistics.setGrabOrderSuccessCount(driverOrderStatistics.getGrabOrderSuccessCount() + 1);
            driverOrderStatisticsMapper.updateById(driverOrderStatistics);
        }

        // 推送逻辑
        // 通知乘客
        JSONObject passengerContent = new JSONObject();
        passengerContent.put("orderId", orderInfo.getId());
        passengerContent.put("driverId", orderInfo.getDriverId());
        passengerContent.put("driverPhone", orderInfo.getDriverPhone());
        passengerContent.put("vehicleNo", orderInfo.getVehicleNo());
        // 车辆信息，调用车辆服务
        ResponseResult<Car> carById = serviceDriverUserClient.getCarById(carId);
        Car carRemote = carById.getData();

        passengerContent.put("brand", carRemote.getBrand());
        passengerContent.put("model", carRemote.getModel());
        passengerContent.put("vehicleColor", carRemote.getVehicleColor());

        passengerContent.put("receiveOrderCarLongitude", orderInfo.getReceiveOrderCarLongitude());
        passengerContent.put("receiveOrderCarLatitude", orderInfo.getReceiveOrderCarLatitude());

        send(orderInfo.getPassengerId(), IdentityEnum.Passenger.getId(), passengerContent.toString());


        return ResponseResult.success();

    }

    public void send(Long id, String identity, String message){
        PushRequest pushRequest = new PushRequest();
        pushRequest.setUserId(id);
        pushRequest.setIdentity(identity);
        pushRequest.setContent(message);

        serviceSsePushClient.push(pushRequest);
    }
}
