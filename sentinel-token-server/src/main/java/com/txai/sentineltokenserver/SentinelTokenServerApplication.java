package com.txai.sentineltokenserver;

import com.alibaba.csp.sentinel.cluster.server.ClusterTokenServer;
import com.alibaba.csp.sentinel.cluster.server.SentinelDefaultTokenServer;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SentinelTokenServerApplication {
    static {
        // sentinel dashboard
        System.setProperty("csp.sentinel.dashboard.server","localhost:8070");
        System.setProperty("project.name","sentinel-token-server");
    }
    public static void main(String[] args) throws Exception {
//        SpringApplication.run(SentinelTokenServerApplication.class, args);

        ClusterTokenServer clusterTokenServer = new SentinelDefaultTokenServer();
        ClusterServerConfigManager.loadGlobalTransportConfig(new ServerTransportConfig().setPort(10121));
        clusterTokenServer.start();
    }
}
