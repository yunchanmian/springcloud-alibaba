package com.gray.level.consumer.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.gray.common.constans.gray.GrayConstant;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Optional;

/**
 * 全链路金丝雀 - Feign 请求拦截器
 * <p>
 * 将当前请求中的灰度相关请求头透传到下游 Feign 调用，保证网关 → consumer → provider 全链路版本一致。
 * 需透传：x-gray-tag、x-gray-context、x-user-id、x-request-id 等。
 * </p>
 *
 * @author guobaihong
 * @date 2026/02/09
 */
@Component
public class GrayFeignRequestInterceptor implements RequestInterceptor {

    /** 需要透传的灰度相关请求头 */
    private static final String[] GRAY_HEADERS = {
            GrayConstant.HEADER_GRAY_TAG,
            GrayConstant.HEADER_GRAY_CONTEXT,
            GrayConstant.HEADER_USER_ID,
            GrayConstant.HEADER_REQUEST_ID,
            GrayConstant.HEADER_GRAY_FLAG,
            GrayConstant.HEADER_SOURCE_SERVICE,
            GrayConstant.HEADER_TRACE_ID,
            GrayConstant.HEADER_SPAN_ID
    };

    @Override
    public void apply(RequestTemplate template) {
        Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(attrs -> attrs instanceof ServletRequestAttributes)
                .map(attrs -> (ServletRequestAttributes) attrs)
                .map(ServletRequestAttributes::getRequest)
                .ifPresent(request -> copyGrayHeaders(request, template));
    }

    private void copyGrayHeaders(HttpServletRequest request, RequestTemplate template) {
        for (String headerName : GRAY_HEADERS) {
            Enumeration<String> values = request.getHeaders(headerName);
            if (values != null && values.hasMoreElements()) {
                template.header(headerName, values.nextElement());
            }
        }
    }
}
