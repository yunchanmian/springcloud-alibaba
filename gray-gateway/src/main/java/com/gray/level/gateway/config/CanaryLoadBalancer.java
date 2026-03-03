package com.gray.level.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gray.common.constans.gray.GrayConstant;
import org.gray.common.pojo.domin.GrayContext;
import org.gray.common.utils.json.JsonUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 自定义负载均衡器
 * @author guobaihong
 */
@Slf4j
public class CanaryLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    private final String serviceId;
    private final Map<String, String> versionCache = new ConcurrentHashMap<>();

    public CanaryLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                              String serviceId) {
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.serviceId = serviceId;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        // 检查 serviceInstanceListSupplierProvider 是否为 null
        if (serviceInstanceListSupplierProvider == null) {
            throw new IllegalStateException("serviceInstanceListSupplierProvider is not initialized");
        }

        // 获取 ServiceInstanceListSupplier 实例
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);

        // 检查 supplier 是否为 null
        return Mono.deferContextual(contextView -> {
            // 从Reactor Context中获取目标版本
            String targetVersion = extractVersionFromContext(contextView, request);
            // 根据灰度版本进行服务实例筛选
            return supplier.get().next()
                    .map(serviceInstances -> processServiceInstances(
                            serviceInstances, targetVersion));
        });
    }

    /**
     * 从多个来源提取版本信息
     *
     * @param contextView 上下文视图对象，用于获取存储在上下文中的版本信息
     * @param request 请求对象，用于从请求头中提取版本信息
     * @return 返回提取到的版本信息，如果未找到则返回默认的稳定版本
     */
    private String extractVersionFromContext(ContextView contextView, Request request) {
        // 方法1: 从请求头中提取版本信息
        String grayVersion = getGrayVersion(request);
        if(StringUtils.isNotBlank(grayVersion)){
            return grayVersion;
        }
        // 方法2: 直接从Context获取（最可靠的方式）
        if (contextView.hasKey(GrayConstant.GRAY_CONTEXT)) {
            Object o = contextView.get(GrayConstant.GRAY_CONTEXT);
            if (o instanceof GrayContext) {
                GrayContext context = (GrayContext) o;
                return context.getVersion();
            }
        }

        // 方法3: 从自定义的上下文对象获取
        if (contextView.hasKey(GrayConstant.HEADER_GRAY_CONTEXT)) {
            Object grayContext = contextView.get(GrayConstant.HEADER_GRAY_CONTEXT);
            try {
                GrayContext context = JsonUtils.parseObject(grayContext.toString() , GrayContext.class);
                if(context == null){
                    return GrayConstant.STABLE_VERSION;
                }
                return context.getVersion();
            }catch (Exception e){
                return GrayConstant.STABLE_VERSION;
            }
        }
        // 默认返回stable版本
        return GrayConstant.STABLE_VERSION;
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
            return headers.getFirst(GrayConstant.HEADER_GRAY_TAG);
        }
        // 若上下文不是RequestDataContext类型，则返回null
        return null;
    }


    /**
     * 处理服务实例选择
     */
    private Response<ServiceInstance> processServiceInstances(List<ServiceInstance> serviceInstances, String targetVersion) {

        if (serviceInstances.isEmpty()) {
            return new EmptyResponse();
        }

        // 根据目标版本筛选实例
        List<ServiceInstance> filteredInstances = filterInstancesByVersion(serviceInstances, targetVersion);

        if (filteredInstances.isEmpty()) {
            // 没有匹配版本的实例时的处理
            return handleNoMatchingInstances(serviceInstances, targetVersion);
        }

        // 负载均衡选择实例
        ServiceInstance selectedInstance = selectInstance(filteredInstances);

        // 缓存选择结果
        versionCache.put(serviceId, targetVersion);

        // 记录选择结果
        logSelectionResult(targetVersion, selectedInstance);

        return new DefaultResponse(selectedInstance);
    }

    /**
     * 根据版本筛选服务实例
     */
    private List<ServiceInstance> filterInstancesByVersion(
            List<ServiceInstance> instances, String targetVersion) {
        return instances.stream()
                .filter(instance -> {
                    Map<String, String> metadata = instance.getMetadata();
                    String instanceVersion = metadata.getOrDefault(GrayConstant.METADATA_VERSION, GrayConstant.STABLE_VERSION);
                    return targetVersion.equals(instanceVersion);
                })
                .collect(Collectors.toList());
    }

    /**
     * 处理没有匹配实例的情况
     */
    private Response<ServiceInstance> handleNoMatchingInstances(
            List<ServiceInstance> allInstances, String targetVersion) {

        System.err.printf(
                "警告: 服务 %s 没有找到版本为 %s 的实例，回退到随机选择%n",
                serviceId, targetVersion);

        ServiceInstance fallbackInstance = selectInstance(allInstances);
        System.err.printf(
                "回退到实例: %s:%d (版本: %s)%n",
                fallbackInstance.getHost(),
                fallbackInstance.getPort(),
                fallbackInstance.getMetadata().getOrDefault(GrayConstant.METADATA_VERSION, "unknown"));

        return new DefaultResponse(fallbackInstance);
    }

    /**
     * 选择实例（随机负载均衡）
     */
    private ServiceInstance selectInstance(List<ServiceInstance> instances) {
        if (instances.size() == 1) {
            return instances.get(0);
        }
        return instances.get(new Random().nextInt(instances.size()));
    }

    /**
     * 记录选择结果
     */
    private void logSelectionResult(String targetVersion, ServiceInstance selectedInstance) {
        String instanceVersion = selectedInstance.getMetadata().getOrDefault(GrayConstant.METADATA_VERSION, "unknown");
        System.out.printf(
                "服务 %s: 目标版本=%s, 选择实例=%s:%d, 实际版本=%s%n",
                serviceId,
                targetVersion,
                selectedInstance.getHost(),
                selectedInstance.getPort(),
                instanceVersion);
    }

    /**
     * 获取缓存的版本信息
     */
    public String getCachedVersion() {
        return versionCache.getOrDefault(serviceId, "unknown");
    }
}
