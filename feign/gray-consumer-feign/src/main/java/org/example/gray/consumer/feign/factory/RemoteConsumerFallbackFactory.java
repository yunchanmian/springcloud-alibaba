package org.example.gray.consumer.feign.factory;

import lombok.extern.slf4j.Slf4j;
import org.example.gray.consumer.feign.service.RemoteConsumerService;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteConsumerFallbackFactory implements FallbackFactory<RemoteConsumerService> {
    @Override
    public RemoteConsumerService create(Throwable cause) {
        return new RemoteConsumerService() {
            @Override
            public String hello(String from) {
                return "请求错误";
            }
        };
    }
}
