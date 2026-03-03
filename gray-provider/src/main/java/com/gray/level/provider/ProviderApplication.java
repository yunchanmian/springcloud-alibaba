package com.gray.level.provider;

import com.gray.level.provider.annotation.EnableRyFeignClients;
import com.gray.level.provider.config.LoadBalancerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * 全链路金丝雀 - 提供者服务启动类
 * 通过 Nacos 元数据 version=stable 或 version=gray 区分版本，网关/消费者负载均衡会按灰度标记路由到此版本。
 * @author guobaihong
 */

@EnableDiscoveryClient
@EnableRyFeignClients
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@Import({ FeignAutoConfiguration.class })
@LoadBalancerClients(defaultConfiguration = LoadBalancerConfig.class)
public class ProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }
}
