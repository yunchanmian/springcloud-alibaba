package com.gray.level.provider.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gray.common.constans.gray.GrayConstant;
import org.gray.common.context.GrayContextHolder;
import org.gray.common.pojo.domin.GrayContext;
import org.gray.common.utils.json.JsonUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
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
public class ProviderGrayLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private final ObjectProvider<ServiceInstanceListSupplier> supplierProvider;
    private final String serviceId;

    public ProviderGrayLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> supplierProvider, String serviceId) {
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

    /**
     * 获取请求中的灰度版本标识。
     *
     * @param request 请求对象，用于获取上下文信息
     * @return 返回灰度版本标识字符串，如果无法获取则返回null
     */
    private String getGrayVersion(Request request) {
        // 检查请求上下文是否为空，若为空则直接返回null
        if (request.getContext() == null) {
            return null;
        }

        // 判断请求上下文是否为RequestDataContext类型
        if (request.getContext() instanceof RequestDataContext) {
            // 强制转换为RequestDataContext类型以访问客户端请求头
            RequestDataContext context = (RequestDataContext) request.getContext();
            // 从请求属性中获取灰度标识，这个属性是在过滤器中设置的
            // 注意：这里我们通过请求的header来传递，但是RequestDataContext中包含了原始请求的headers，我们也可以从那里获取。
            // 但是我们在过滤器中已经将灰度标识放入了exchange的属性中，而exchange的属性在负载均衡时如何传递呢？
            // 实际上，负载均衡的Request上下文是RequestDataContext，它包含了原始请求的headers，所以我们直接从这里获取。
            // 但是我们的过滤器是在网关层面，而负载均衡是在网关之后，所以我们需要将灰度标识传递到负载均衡的请求上下文中。
            // 在过滤器中，我们将灰度标识放入了exchange的属性，但是负载均衡时使用的是RequestDataContext，它并不包含exchange的属性。
            // 因此，我们需要通过其他方式传递。一种方法是将灰度标识放入请求头，然后在这里从请求头中获取。
            // 但是注意：网关转发到下游服务时，我们可能希望将灰度标识传递给下游，所以我们可以将灰度标识继续放入请求头。
            // 但是负载均衡器需要根据灰度标识选择实例，所以我们需要在负载均衡器中获取这个灰度标识。
            // 这里我们选择从请求头中获取，因为我们在过滤器中已经将灰度标识放入了请求头（如果不希望传递给下游，可以在转发前去掉，但这里我们需要）。
            // 所以，我们在过滤器中已经将灰度标识放入请求头，那么负载均衡器可以从RequestDataContext的headers中获取。
            HttpHeaders headers = context.getClientRequest().getHeaders();

            // 从请求头中获取灰度标签并返回
            String first = headers.getFirst(GrayConstant.HEADER_GRAY_TAG);
//            GrayContext grayContext = JsonUtils.parseObject(first, GrayContext.class);
            return first;
        }
        // 若上下文不是RequestDataContext类型，则返回null
        return null;
    }

    private GrayContext resolveGrayContext(Request request, ContextView contextView) {
        if (request != null && request.getContext() != null) {
            // 方法1: 从请求头中提取版本信息
            GrayContext grayContext = new GrayContext();
            String grayVersion = getGrayVersion(request);
            if(StringUtils.isNotBlank(grayVersion)){
                grayContext.setVersion(grayVersion);
                return grayContext;
            }
            // 方法2: 直接从Context获取（最可靠的方式）
            if (contextView.hasKey(GrayConstant.GRAY_CONTEXT)) {
                Object o = contextView.get(GrayConstant.GRAY_CONTEXT);
                if (o instanceof GrayContext) {
                    GrayContext context = (GrayContext) o;
                }
            }

            // 方法3: 从自定义的上下文对象获取
            if (contextView.hasKey(GrayConstant.HEADER_GRAY_CONTEXT)) {
                Object object = contextView.get(GrayConstant.HEADER_GRAY_CONTEXT);
                try {
                    grayContext = JsonUtils.parseObject(object.toString() , GrayContext.class);
                    if(grayContext == null){
                        throw new RuntimeException("灰度上下问解析异常");
                    }
                }catch (Exception e){
                    return null;
                }
            }
            try {
                String raw = String.valueOf(request.getContext());
                if (!"null".equals(raw)) {
                    return JsonUtils.parseObject(raw, GrayContext.class);
                }
            } catch (Exception ignored) {
            }
        }
        try {
            Object fromReactor = contextView.getOrDefault(GrayConstant.GRAY_CONTEXT, null);
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
        String targetVersion = grayContext.getVersion();
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
