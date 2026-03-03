package com.gray.level.consumer.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 全链路金丝雀 - 消费者侧灰度负载均衡配置
 * 为 Feign 调用 gray-provider 时按 GrayContextHolder 中的灰度标记选择 stable/gray 实例。
 * LoadBalancerClientFactory 使用 spring-cloud-loadbalancer 包下的 org.springframework.cloud.loadbalancer.support。
 */
@Configuration
public class ConsumerGrayLoadBalancerConfiguration {

    @Bean
    public ReactorServiceInstanceLoadBalancer consumerGrayReactorLoadBalancer(
            ObjectProvider<ServiceInstanceListSupplier> supplierProvider,
            Environment environment) {
        String serviceId = LoadBalancerClientFactory.getName(environment);
        return new ConsumerGrayLoadBalancer(supplierProvider, serviceId);
    }
}
