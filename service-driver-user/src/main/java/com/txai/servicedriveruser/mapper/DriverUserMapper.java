package com.txai.servicedriveruser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.txai.common.dto.DriverUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverUserMapper extends BaseMapper<DriverUser> {

    @Select("select count(1) from driver_user where address = #{address} and state = 1")
    public int selectDriverUserCountByCityCode(@Param("address") String address);
}
