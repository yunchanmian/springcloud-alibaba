package com.gray.level.consumer.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 全链路金丝雀 - 调用 gray-provider 的 Feign 客户端
 * 请求会携带灰度请求头（由 GrayFeignRequestInterceptor 注入），LoadBalancer 会路由到对应版本实例。
 */
@FeignClient(name = "gray-provider")
public interface GrayProviderClient {

    /**
     * 调用提供者 /product/hello 接口
     *
     * @param from 调用方标识，便于日志追踪
     * @return 提供者返回的版本信息
     */
    @GetMapping("/product/hello")
    String hello(@RequestParam(value = "from", defaultValue = "consumer") String from);
}
