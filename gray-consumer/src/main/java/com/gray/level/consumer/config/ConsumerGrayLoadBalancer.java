package com.gray.level.consumer.config;

import lombok.extern.slf4j.Slf4j;
import org.gray.common.constans.gray.GrayConstant;
import org.gray.common.context.GrayContextHolder;
import org.gray.common.pojo.domin.GrayContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 全链路金丝雀 - 消费者侧灰度负载均衡器
 * <p>
 * 从 GrayContextHolder（由 GrayContextFilter 从请求头注入）或 Request/Reactor 上下文读取灰度标记，
 * 将 Feign 对 gray-provider 的调用路由到对应版本实例。
 * </p>
 *
 * @author guobaihong
 * @date 2026/02/09
 */
@Slf4j
public class ConsumerGrayLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private final ObjectProvider<ServiceInstanceListSupplier> supplierProvider;
    private final String serviceId;

    public ConsumerGrayLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> supplierProvider, String serviceId) {
        this.supplierProvider = supplierProvider;
        this.serviceId = serviceId;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = supplierProvider.getIfAvailable();
        if (supplier == null) {
            return Mono.just(new EmptyResponse());
        }
        log.debug("消费者灰度负载均衡，服务名：{}", serviceId);
        return Mono.deferContextual(ctxView -> {
            GrayContext grayContext = resolveGrayContext(request, ctxView);
            return supplier.get(request).next()
                    .map(instances -> processInstanceResponse(instances, grayContext));
        });
    }

    private GrayContext resolveGrayContext(Request request, ContextView reactorContext) {
        if (request != null && request.getContext() != null) {
            try {
                String raw = String.valueOf(request.getContext());
                if (!"null".equals(raw)) {
                    return org.gray.common.utils.json.JsonUtils.parseObject(raw, GrayContext.class);
                }
            } catch (Exception ignored) {
            }
        }
        try {
            Object fromReactor = reactorContext.getOrDefault(GrayConstant.GRAY_CONTEXT, null);
            if (fromReactor instanceof GrayContext) {
                return (GrayContext) fromReactor;
            }
        } catch (Exception ignored) {
        }
        return GrayContextHolder.get();
    }

    private Response<ServiceInstance> processInstanceResponse(List<ServiceInstance> serviceInstanceList, GrayContext grayContext) {
        if (serviceInstanceList == null || serviceInstanceList.isEmpty()) {
            return new EmptyResponse();
        }
        String targetVersion = (grayContext != null && Boolean.TRUE.equals(grayContext.getGray()))
                ? GrayConstant.GRAY_VERSION
                : GrayConstant.STABLE_VERSION;
        // 存储匹配结果
        List<ServiceInstance> matched = new ArrayList<>();

        // 一次遍历完成筛选逻辑
        for (ServiceInstance instance : serviceInstanceList) {
            Map<String, String> metadata = instance.getMetadata();
            if (metadata == null) {
                // 元数据为空则跳过
                continue;
            }
            String version = metadata.get(GrayConstant.METADATA_VERSION);
            if (targetVersion.equals(version)) {
                // 匹配目标版本
                matched.add(instance);
            } else if (GrayConstant.STABLE_VERSION.equals(version) && matched.isEmpty()) {
                // 备选稳定版本
                matched.add(instance);
            }
        }
        AtomicReference<Map<String, String>> meta;
//        List<ServiceInstance> matched = serviceInstanceList.stream()
//                .filter(instance -> targetVersion.equals((meta.set(instance.getMetadata())) != null ? meta.get().get(GrayConstant.METADATA_VERSION) : null))
//                .collect(Collectors.toList());
//        if (matched.isEmpty()) {
//            matched = serviceInstanceList.stream()
//                    .filter(instance -> GrayConstant.STABLE_VERSION.equals(instance.getMetadata() != null ? instance.getMetadata().get(GrayConstant.METADATA_VERSION) : null))
//                    .collect(Collectors.toList());
//        }
        if (matched.isEmpty()) {
            return new EmptyResponse();
        }
        return new DefaultResponse(matched.get(ThreadLocalRandom.current().nextInt(matched.size())));
    }
}
