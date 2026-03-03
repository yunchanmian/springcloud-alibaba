package com.gray.level.consumer.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gray.common.constans.gray.GrayConstant;
import org.gray.common.context.GrayContextHolder;
import org.gray.common.pojo.domin.GrayContext;
import org.gray.common.utils.json.JsonUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 全链路金丝雀 - 灰度上下文过滤器
 * <p>
 * 从网关下发的请求头 x-gray-context 解析 GrayContext 并放入 GrayContextHolder，
 * 供本服务 LoadBalancer、Feign 拦截器使用；请求结束后清理，避免线程池复用时泄漏。
 * </p>
 *
 * @author guobaihong
 * @date 2026/02/09
 */
@Slf4j
@Component
@Order(-1000)
public class GrayContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String grayContextJson = request.getHeader(GrayConstant.HEADER_GRAY_CONTEXT);
            if (StringUtils.isNotBlank(grayContextJson)) {
                try {
                    log.info("gray-context: {}", grayContextJson);
                    if(JsonUtils.isJson(grayContextJson)){
                        GrayContext ctx = JsonUtils.parseObject(grayContextJson, GrayContext.class);
                        if (ctx != null) {
                            GrayContextHolder.set(ctx);
                        }
                    }
                    log.warn("Invalid gray-context format detected: [REDACTED]");
                } catch (Exception ignored) {
                    log.error("gray-context parse err: {}", grayContextJson);
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            GrayContextHolder.clear();
        }
    }
}
