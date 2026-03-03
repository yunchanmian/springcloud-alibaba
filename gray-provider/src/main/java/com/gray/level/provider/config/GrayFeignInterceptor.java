package com.gray.level.provider.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.gray.common.constans.gray.GrayConstant;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author guobaihong
 */
@Slf4j
@Component
public class GrayFeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String grayVersion = request.getHeader(GrayConstant.HEADER_GRAY_TAG);
            String header = request.getHeader(GrayConstant.HEADER_GRAY_CONTEXT);
            log.info(grayVersion);
            log.info(header);
            if (grayVersion != null && !grayVersion.isEmpty()) {
                requestTemplate.header(GrayConstant.HEADER_GRAY_TAG, grayVersion);
            }
        }
    }
}
