package com.txai.nacosclustertesting.controller;

import com.txai.nacosclustertesting.util.EncryptDecryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RefreshScope
@RestController
public class TestController {

    @Value("${ceshi}")
    private String ceshi;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/test")
    public String test() {
        log.info(ceshi);
        return "test-"+ceshi+"-" + System.currentTimeMillis();
    }

    @GetMapping("/redis")
    public String redis() {
        String aa_bb_cc = "aa_bb_cc";
        String s = redisTemplate.opsForValue().get(aa_bb_cc);
        if (!StringUtils.hasLength(s)) {
            log.info("The key nof found");
            redisTemplate.opsForValue().set(aa_bb_cc, aa_bb_cc + System.currentTimeMillis());
        } else {
            log.info("The key exists, value=" + s);
        }
        return s + System.currentTimeMillis();
    }

}
