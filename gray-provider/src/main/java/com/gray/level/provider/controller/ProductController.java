package com.gray.level.provider.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.gray.consumer.feign.service.RemoteConsumerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * 全链路金丝雀 - 提供者对外接口
 * 返回当前实例的版本信息，用于验证请求是否命中 stable/gray 实例。
 */
@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    /** 当前实例在 Nacos 的版本（stable / gray），由配置或 profile 决定 */
    @Value("${spring.cloud.nacos.discovery.metadata.version:stable}")
    private String version;

    @Resource
    private RemoteConsumerService remoteConsumerService;

    /**
     * 示例接口：返回版本信息，便于验证全链路灰度（Feign 调用返回字符串）
     */
    @GetMapping("/hello")
    public String hello(@RequestParam(value = "from", defaultValue = "unknown") String from) {
        String hello = remoteConsumerService.hello(from);
        log.info("gray-provider-{} 收到请求 from={}, hello={}", version, from, hello);
        return "gray-provider-" + version + "(from=" + from + ")";
    }

    /**
     * 返回当前实例版本标识，便于前端或网关区分
     */
    @GetMapping("/version")
    public String version() {
        return version;
    }
}
