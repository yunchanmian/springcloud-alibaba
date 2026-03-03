package com.gray.level.gateway.filter;

import com.gray.level.gateway.config.GrayRuleManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gray.common.constans.gray.GrayConstant;
import org.gray.common.pojo.domin.GrayContext;
import org.gray.common.utils.json.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author guobaihong
 */
@Slf4j
@Component
public class GrayLoadBalancerFilter implements GlobalFilter, Ordered {

    @Resource
    private GrayRuleManager grayRuleManager;

    @Value("${spring.cloud.nacos.discovery.group:DEFAULT_GROUP}")
    private String group;


    /**
     * 过滤器方法，用于处理网关请求中的灰度发布逻辑。
     *
     * @param exchange 当前的服务器Web交换对象，包含请求和响应信息
     * @param chain 网关过滤器链，用于继续执行后续过滤器
     * @return 返回一个Mono<Void>，表示异步处理完成后的结果
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 1、判断当前请求是否为灰度请求
        boolean isGrayRequest = isGrayRequest(request);

        // 2、构建用于整个请求链路中的灰度传输对象，并将其存储到exchange属性中
        GrayContext grayContext = buildGrayContext(request, isGrayRequest);
        exchange.getAttributes().put(GrayConstant.GRAY_CONTEXT, grayContext);

        // 3、在请求头中添加灰度标记，供下游服务与负载均衡使用
        ServerHttpRequest grayRequest = request.mutate()
                .header(GrayConstant.HEADER_GRAY_TAG, grayContext.getGray() != null && grayContext.getGray() ? GrayConstant.GRAY_VERSION : GrayConstant.STABLE_VERSION)
                .header(GrayConstant.HEADER_GRAY_CONTEXT, JsonUtils.toJsonString(grayContext))
                .build();

        // 4、将 GrayContext 写入 Reactor 上下文，供 GrayLoadBalancer.choose() 在同一响应式链中读取
        return chain.filter(exchange.mutate().request(grayRequest).build())
                .contextWrite(reactor.util.context.Context.of(GrayConstant.GRAY_CONTEXT, grayContext));
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }


    /**
     * 判断当前请求是否为灰度请求。
     *
     * @param request 当前的HTTP请求对象，用于获取请求头信息
     * @return true 表示该请求符合灰度规则，false 表示不符合
     */
    private boolean isGrayRequest(ServerHttpRequest request) {
        // 缓存请求头以避免重复调用
        HttpHeaders headers = request.getHeaders();

        // 1、灰度检查规则一：检查请求头中的灰度标识
        if (headers.containsKey(GrayConstant.HEADER_GRAY_TAG)) {
            String grayTag = headers.getFirst(GrayConstant.HEADER_GRAY_TAG);
            log.info("命中灰度规则一：请求头包含灰度标识 {}", grayTag);
            return GrayConstant.isGrayRequest(grayTag);
        }

        // 2、灰度检查规则二：根据用户ID灰度规则判断
        if (headers.containsKey(GrayConstant.HEADER_USER_ID)) {
            String userId = headers.getFirst(GrayConstant.HEADER_USER_ID);
            if (StringUtils.isNotBlank(userId) && grayRuleManager.isUserInGrayList(userId)) {
                log.info("命中灰度规则二：用户ID {} 在灰度列表中", userId);
                return true;
            }
        }
        log.debug("未存储特殊规则，进行随机流量分配");
        // 3、灰度检查规则三：根据百分比灰度规则判断
        return grayRuleManager.isGrayByPercentage();
    }

    /**
     * 构建灰度上下文对象。
     *
     * @param request 当前HTTP请求对象，用于提取请求头、远程地址等信息。
     * @param isGray  布尔值，标识当前请求是否处于灰度环境中。
     * @return 返回构建完成的灰度上下文对象，包含请求ID、用户ID、来源IP、版本等信息。
     */
    private GrayContext buildGrayContext(ServerHttpRequest request, boolean isGray){
        // 创建一个新的灰度上下文对象
        GrayContext context = new GrayContext();
        // 设置是否为灰度环境
        context.setGray(isGray);
        // 生成唯一的请求ID并设置到上下文中
        context.setRequestId(UUID.randomUUID().toString());
        // 从请求头中提取用户ID并设置到上下文中
        context.setUserId(request.getHeaders().getFirst(GrayConstant.HEADER_USER_ID));
        // 获取请求的来源IP地址，若无法获取则设置为"unknown"
        context.setSourceIp(request.getRemoteAddress() != null ? request.getRemoteAddress().getHostString() : "unknown");
        // 根据是否为灰度环境设置对应的版本号
        context.setVersion(isGray ? GrayConstant.GRAY_VERSION : GrayConstant.STABLE_VERSION);
        // 返回构建完成的灰度上下文对象
        return context;
    }


}
