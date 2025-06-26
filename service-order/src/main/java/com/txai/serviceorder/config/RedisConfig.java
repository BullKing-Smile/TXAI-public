package com.txai.serviceorder.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class RedisConfig {

    private String potocol = "redis://";

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private String redisPort;

    @Value("${spring.data.redis.password}")
    private String password;

    @Bean("redissonBootYml")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(potocol + redisHost + ":" + redisPort)
                .setPassword(password)
                .setDatabase(0);

        return Redisson.create(config);

    }

    @Bean("redissonYamlClient")
    public RedissonClient redissonYamlClient() {
        Config config = null;
        try {
            config = Config.fromYAML(new ClassPathResource("/redisson-config/single-server.yaml").getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Redisson.create(config);
        // 仅使用下面代码， 便于测试
//        return Redisson.create();
    }

    @Bean("redissonMasterSlaveClient")
    public RedissonClient redissonMasterSlaveClient(){
        Config config = null;
        try {
            config = Config.fromYAML(new ClassPathResource("/redisson-config/master-slave-server.yaml").getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }


        return Redisson.create(config);
        // 便于测试
//        return Redisson.create();
    }

    @Bean("redissonSentinelClient")
    public RedissonClient redissonSentinelClient(){
        Config config = null;
        try {
            config = Config.fromYAML(new ClassPathResource("/redisson-config/sentinel.yaml").getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }


        return Redisson.create(config);

        // 便于测试
//        return Redisson.create();

    }

    @Bean("redissonClusterClient")
    public RedissonClient redissonClusterClient(){
        Config config = null;
        try {
            config = Config.fromYAML(new ClassPathResource("/redisson-config/cluster.yaml").getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }


        return Redisson.create(config);

        // 便于测试
//        return Redisson.create();



    }
}
