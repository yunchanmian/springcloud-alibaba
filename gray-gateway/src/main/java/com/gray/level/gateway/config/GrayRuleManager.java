package com.gray.level.gateway.config;

import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 全链路金丝雀发布 - 灰度规则管理器
 * <p>
 * 支持：用户 ID 白名单、用户 ID 百分比、Nacos 配置热更新。
 * 与 gray-rules.yaml / Nacos gray-rule 配置对应。
 * </p>
 *
 * @author guobaihong
 * @date 2026/02/09
 */
@Component
@RefreshScope
public class GrayRuleManager {

    @Value("${gray.rules.user-id.percentage:10}")
    private Integer userPercentage;

    @Value("${gray.rules.user-id.whitelist:#{T(java.util.Collections).emptyList()}}")
    private List<String> userWhitelist;

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    private List<String> safeWhitelist() {
        return userWhitelist != null ? userWhitelist : Collections.emptyList();
    }

    /**
     * 判断指定用户是否在灰度名单中。
     *
     * @param userId 用户唯一标识符
     * @return 如果用户在白名单中或满足百分比灰度条件，则返回true；否则返回false
     */
    public boolean isUserInGrayList(String userId){
        // 检查用户是否在白名单中，如果在则直接返回true
        if (safeWhitelist().contains(userId)) {
            return true;
        }

        // 如果设置了百分比灰度，则根据用户ID的哈希值进行判断
        if(userPercentage > 0){
            try {
                int userIdHash = Math.abs(userId.hashCode());
                // 根据哈希值取模100，判断是否小于设定的百分比
                return userIdHash % 100 < userPercentage;
            }catch (Exception e){
                // 发生异常时默认返回false
                return false;
            }
        }

        // 默认情况下返回false
        return false;
    }

    /**
     * 判断是否根据用户百分比随机变为灰色。
     * <p>
     * 该方法通过比较用户设定的百分比与一个随机数来决定是否返回true。
     * 如果用户百分比大于0，并且生成的随机数小于该百分比，则返回true，
     * 表示应该变为灰色；否则返回false。
     *
     * @return true 如果根据百分比判断应变为灰色，否则返回false
     */
    public boolean isGrayByPercentage(){
        // 检查用户百分比是否大于0，并结合随机数判断是否满足变灰条件
        return userPercentage > 0 && RANDOM.nextInt(100) < userPercentage;
    }

    /**
     * 监听 Nacos 中灰度规则变更，实现热更新（可选，需 Nacos Config）。
     * <p>
     * 当灰度规则配置文件被更新时，该方法会被调用。
     *
     * @param newRules 新的灰度规则配置文件内容
     */
    @NacosConfigListener(dataId = "gray-rules.yaml", groupId = "DEFAULT_GROUP")
    public void onGrayRulesUpdated(String newRules) {
        // 可解析 newRules 更新 userPercentage、userWhitelist 等
    }
}
