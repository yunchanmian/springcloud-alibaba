package org.example.gray.consumer.feign.service;

import org.example.gray.consumer.feign.factory.RemoteConsumerFallbackFactory;
import org.gray.common.constans.feign.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务
 *
 * @author yunchanmian
 */
@FeignClient(contextId = "remoteConsumerService", value = ServiceNameConstants.GRAY_CONSUMER, fallbackFactory = RemoteConsumerFallbackFactory.class)
public interface RemoteConsumerService {

    /**
     * 示例：聚合调用 gray-provider，用于验证全链路灰度（网关 → consumer → provider 版本一致）
     */
    @GetMapping("/gray-consumer/hello")
    String hello(@RequestParam(value = "from", defaultValue = "gateway") String from);
}
