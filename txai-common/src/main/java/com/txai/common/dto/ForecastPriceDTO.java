package com.txai.common.dto;

import com.txai.common.constraints.VehicleTypeCheck;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ForecastPriceDTO {
    @Pattern(regexp = "^[\\-\\+]?(\\d{1,2}\\.\\d{1,5}|1[0-7]\\d\\.\\d{1,5}|\\d{1,2}|1[0-7]\\d|180\\.0{1,5}|180)$", message = "起点经度格式错误")
    private String depLongitude;
    @Pattern(regexp = "^[\\-\\+]?(([0-8]?\\d\\.\\d{1,5}|90\\.0{1,5})|([0-8]?\\d|90))$", message = "起点纬度格式错误")
    private String depLatitude;
    @Pattern(regexp = "^[\\-\\+]?(\\d{1,2}\\.\\d{1,5}|1[0-7]\\d\\.\\d{1,5}|\\d{1,2}|1[0-7]\\d|180\\.0{1,5}|180)$", message = "终点经度格式错误")
    private String destLongitude;
    @Pattern(regexp = "^[\\-\\+]?(([0-8]?\\d\\.\\d{1,5}|90\\.0{1,5})|([0-8]?\\d|90))$", message = "终点纬度格式错误")
    private String destLatitude;

    @NotBlank(message = "city code not empty")
    @Pattern(regexp = "^\\d{6}$", message = "please input correct city code")
    private String cityCode;

    @VehicleTypeCheck(vehicleTypeValue = {"1","2"})
    @NotBlank(message = "vehicle not empty")
    @Pattern(regexp = "^\\d$", message = "please input correct vehicle type")
    private String vehicleType;
}
