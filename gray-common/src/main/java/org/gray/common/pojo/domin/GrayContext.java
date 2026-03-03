package org.gray.common.pojo.domin;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author guobaihong
 */
@Data
public class GrayContext implements Serializable {

    /** 是否是灰度请求 */
    private Boolean gray;

    /** 请求ID */
    private String requestId;

    /** 用户ID */
    private String userId;

    /** 用户IP */
    private String sourceIp;

    /** 版本 */
    private String version;

    /**
     * 判断当前请求是否为灰度请求（全链路金丝雀标记）
     *
     * @return true 表示灰度请求，false 表示稳定版请求
     */
    public boolean isGray() {
        return Boolean.TRUE.equals(gray);
    }
}
