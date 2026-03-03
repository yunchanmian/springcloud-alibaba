package org.gray.common.context;

import org.gray.common.pojo.domin.GrayContext;

/**
 * 全链路金丝雀 - 灰度上下文线程局部变量
 * <p>
 * 在非网关服务（如 gray-consumer）中，从请求头解析出 GrayContext 后存入此处，
 * 供本服务的 LoadBalancer、业务代码在同步调用链中获取，保证 Feign 调用下游时仍能按灰度路由。
 * 使用后需在 Filter 或 Interceptor 中 clear() 避免线程池复用时泄漏。
 * </p>
 *
 * @author guobaihong
 * @date 2026/02/09
 */
public final class GrayContextHolder {

    private static final ThreadLocal<GrayContext> HOLDER = new ThreadLocal<>();

    public static void set(GrayContext context) {
        HOLDER.set(context);
    }

    public static GrayContext get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }

}
