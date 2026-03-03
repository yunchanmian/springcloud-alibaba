package com.gray.level.consumer.controller;

import com.gray.level.consumer.feign.GrayProviderClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 全链路金丝雀 - 消费者对外接口
 * 网关 /api/consumer/** 会路由到此服务；本服务再通过 Feign 调用 gray-provider，灰度头会透传。
 */
@Slf4j
@RestController
@RequestMapping("/consumer")
@RequiredArgsConstructor
public class ConsumerController {

    private final GrayProviderClient grayProviderClient;

    /**
     * 示例：聚合调用 gray-provider，用于验证全链路灰度（网关 → consumer → provider 版本一致）
     */
    @GetMapping("/hello")
    public String hello(@RequestParam(value = "from", defaultValue = "gateway") String from) {
        log.info("consumer 收到请求 from={}", from);
//        String providerResponse = grayProviderClient.hello("consumer");
        return "consumer -> ";
    }
}
