package com.txai.servicepassengeruser.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("passenger_user")
public class PassengerUser {
    private Long id;
    private String passengerName;
    private String passengerPhone;
    private byte passengerGender;
    private byte state;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
