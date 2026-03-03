package com.gray.level.consumer;

import com.gray.level.consumer.config.ConsumerGrayLoadBalancerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 全链路金丝雀 - 消费者服务启动类
 * 通过 Feign 调用 gray-provider，并透传灰度请求头；LoadBalancer 按 GrayContextHolder 选择 provider 版本。
 * @author guobaihong
 */
@EnableDiscoveryClient
@EnableFeignClients
@LoadBalancerClients(defaultConfiguration = ConsumerGrayLoadBalancerConfiguration.class)
@SpringBootApplication
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
