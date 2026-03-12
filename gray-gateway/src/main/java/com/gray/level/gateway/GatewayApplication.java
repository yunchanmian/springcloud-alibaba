package com.gray.level.gateway;

import com.gray.level.gateway.config.LoadBalancerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;

/**
 * 全链路金丝雀发布 - 网关启动类
 * 启用 Nacos 服务发现，并为所有 lb:// 路由启用灰度负载均衡。
 * @author guobaihong
 */
@EnableDiscoveryClient
@SpringBootApplication
@LoadBalancerClients(defaultConfiguration = LoadBalancerConfig.class)
public class GatewayApplication {
    public static void main(String[] args) {
        System.setProperty("nacos.logging.default.config.enabled","false");
        SpringApplication.run(GatewayApplication.class,args);
    }
}
