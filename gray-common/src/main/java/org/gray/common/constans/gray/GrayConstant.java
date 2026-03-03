package org.gray.common.constans.gray;

public class GrayConstant {

    // ================== 版本常量 ==================
    /**
     * 稳定版本标识
     */
    public static final String STABLE_VERSION = "stable";

    /**
     * 灰度版本标识
     */
    public static final String GRAY_VERSION = "gray";

    /**
     * 默认版本
     */
    public static final String DEFAULT_VERSION = STABLE_VERSION;

    // ================== 请求头常量 ==================
    /**
     * 灰度标记请求头
     */
    public static final String HEADER_GRAY_TAG = "x-gray-tag";

    /**
     * 灰度上下文请求头
     */
    public static final String HEADER_GRAY_CONTEXT = "x-gray-context";

    /**
     * 请求ID请求头
     */
    public static final String HEADER_REQUEST_ID = "x-request-id";

    /**
     * 用户ID请求头
     */
    public static final String HEADER_USER_ID = "x-user-id";

    /**
     * 灰度标志请求头
     */
    public static final String HEADER_GRAY_FLAG = "x-gray-flag";

    /**
     * 源服务名称请求头
     */
    public static final String HEADER_SOURCE_SERVICE = "x-source-service";

    /**
     * 全链路追踪ID
     */
    public static final String HEADER_TRACE_ID = "x-b3-traceid";

    /**
     * 调用链跨度ID
     */
    public static final String HEADER_SPAN_ID = "x-b3-spanid";

    // ================== 元数据常量 ==================
    /**
     * 版本元数据key
     */
    public static final String METADATA_VERSION = "version";

    /**
     * 灰度环境元数据key
     */
    public static final String METADATA_GRAY_ENABLED = "gray.enabled";

    /**
     * 环境标识元数据key
     */
    public static final String METADATA_ENV = "env";

    /**
     * 区域元数据key
     */
    public static final String METADATA_REGION = "region";

    /**
     * 可用区元数据key
     */
    public static final String METADATA_ZONE = "zone";

    /**
     * 集群元数据key
     */
    public static final String METADATA_CLUSTER = "cluster";

    /**
     * 权重元数据key
     */
    public static final String METADATA_WEIGHT = "weight";

    /**
     * 实例组元数据key
     */
    public static final String METADATA_GROUP = "group";

    // ================== 上下文常量 ==================
    /**
     * 灰度上下文属性名
     */
    public static final String GRAY_CONTEXT = "GRAY_CONTEXT";

    /**
     * 灰度规则上下文
     */
    public static final String GRAY_RULE_CONTEXT = "GRAY_RULE_CONTEXT";

    /**
     * 负载均衡上下文
     */
    public static final String LOADBALANCER_CONTEXT = "loadBalancerContext";

    // ================== 配置常量 ==================
    /**
     * 灰度规则配置dataId
     */
    public static final String GRAY_RULES_DATA_ID = "gray-rules.yaml";

    /**
     * 灰度规则配置group
     */
    public static final String GRAY_RULES_GROUP = "GRAY_GROUP";

    /**
     * 灰度规则配置默认组
     */
    public static final String DEFAULT_GROUP = "DEFAULT_GROUP";

    /**
     * 灰度开关配置key
     */
    public static final String CONFIG_GRAY_ENABLED = "gray.enabled";

    /**
     * 灰度百分比配置key
     */
    public static final String CONFIG_GRAY_PERCENTAGE = "gray.rules.user-id.percentage";

    /**
     * 灰度白名单配置key
     */
    public static final String CONFIG_GRAY_WHITELIST = "gray.rules.user-id.whitelist";

    /**
     * 灰度规则类型配置key
     */
    public static final String CONFIG_GRAY_RULE_TYPE = "gray.rules.user-id.type";

    // ================== 规则类型常量 ==================
    /**
     * 基于百分比的灰度规则
     */
    public static final String RULE_TYPE_PERCENTAGE = "percentage";

    /**
     * 基于白名单的灰度规则
     */
    public static final String RULE_TYPE_WHITELIST = "whitelist";

    /**
     * 基于请求头的灰度规则
     */
    public static final String RULE_TYPE_HEADER = "header";

    /**
     * 基于IP的灰度规则
     */
    public static final String RULE_TYPE_IP = "ip";

    /**
     * 基于Cookie的灰度规则
     */
    public static final String RULE_TYPE_COOKIE = "cookie";

    /**
     * 基于参数的灰度规则
     */
    public static final String RULE_TYPE_PARAM = "param";

    // ================== 路由常量 ==================
    /**
     * 灰度路由优先级
     */
    public static final int GRAY_ROUTE_ORDER = 1000;

    /**
     * 默认路由优先级
     */
    public static final int DEFAULT_ROUTE_ORDER = 10000;

    /**
     * 灰度路由ID前缀
     */
    public static final String GRAY_ROUTE_PREFIX = "gray_route_";

    /**
     * 稳定路由ID前缀
     */
    public static final String STABLE_ROUTE_PREFIX = "stable_route_";

    // ================== 负载均衡常量 ==================
    /**
     * 灰度负载均衡策略
     */
    public static final String LOADBALANCER_STRATEGY_GRAY = "gray";

    /**
     * 版本优先负载均衡策略
     */
    public static final String LOADBALANCER_STRATEGY_VERSION_FIRST = "version-first";

    /**
     * 权重负载均衡策略
     */
    public static final String LOADBALANCER_STRATEGY_WEIGHT = "weight";

    // ================== 缓存常量 ==================
    /**
     * 灰度规则缓存key
     */
    public static final String CACHE_GRAY_RULES = "gray:rules";

    /**
     * 灰度实例缓存key
     */
    public static final String CACHE_GRAY_INSTANCES = "gray:instances";

    /**
     * 灰度用户缓存key
     */
    public static final String CACHE_GRAY_USERS = "gray:users";

    /**
     * 缓存过期时间（秒）
     */
    public static final int CACHE_EXPIRE_SECONDS = 300;

    // ================== 监控常量 ==================
    /**
     * 灰度请求监控指标前缀
     */
    public static final String METRIC_GRAY_PREFIX = "gray.requests";

    /**
     * 灰度版本请求数指标
     */
    public static final String METRIC_GRAY_VERSION_REQUESTS = METRIC_GRAY_PREFIX + ".version";

    /**
     * 灰度服务请求数指标
     */
    public static final String METRIC_GRAY_SERVICE_REQUESTS = METRIC_GRAY_PREFIX + ".service";

    /**
     * 灰度规则命中数指标
     */
    public static final String METRIC_GRAY_RULE_HITS = METRIC_GRAY_PREFIX + ".rule.hits";

    /**
     * 灰度请求延迟指标
     */
    public static final String METRIC_GRAY_REQUEST_LATENCY = METRIC_GRAY_PREFIX + ".latency";

    /**
     * 灰度错误数指标
     */
    public static final String METRIC_GRAY_ERRORS = METRIC_GRAY_PREFIX + ".errors";

    // ================== 线程上下文常量 ==================
    /**
     * 线程上下文灰度标记key
     */
    public static final String THREAD_CONTEXT_GRAY_FLAG = "THREAD_GRAY_FLAG";

    /**
     * 线程上下文请求ID key
     */
    public static final String THREAD_CONTEXT_REQUEST_ID = "THREAD_REQUEST_ID";

    /**
     * 线程上下文用户ID key
     */
    public static final String THREAD_CONTEXT_USER_ID = "THREAD_USER_ID";

    /**
     * 线程上下文版本key
     */
    public static final String THREAD_CONTEXT_VERSION = "THREAD_VERSION";

    // ================== 错误码常量 ==================
    /**
     * 灰度规则错误码前缀
     */
    public static final String ERROR_CODE_PREFIX = "GRAY_";

    /**
     * 灰度规则配置错误
     */
    public static final String ERROR_CODE_CONFIG_ERROR = ERROR_CODE_PREFIX + "001";

    /**
     * 灰度规则解析错误
     */
    public static final String ERROR_CODE_RULE_PARSE_ERROR = ERROR_CODE_PREFIX + "002";

    /**
     * 灰度实例不存在错误
     */
    public static final String ERROR_CODE_INSTANCE_NOT_FOUND = ERROR_CODE_PREFIX + "003";

    /**
     * 灰度上下文缺失错误
     */
    public static final String ERROR_CODE_CONTEXT_MISSING = ERROR_CODE_PREFIX + "004";

    /**
     * 灰度路由配置错误
     */
    public static final String ERROR_CODE_ROUTE_CONFIG_ERROR = ERROR_CODE_PREFIX + "005";

    /**
     * 灰度服务调用错误
     */
    public static final String ERROR_CODE_SERVICE_CALL_ERROR = ERROR_CODE_PREFIX + "006";

    // ================== 日期格式常量 ==================
    /**
     * 标准日期时间格式
     */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 标准日期格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 标准时间格式
     */
    public static final String TIME_FORMAT = "HH:mm:ss";

    // ================== 默认值常量 ==================
    /**
     * 默认灰度百分比
     */
    public static final int DEFAULT_GRAY_PERCENTAGE = 10;

    /**
     * 默认版本权重
     */
    public static final int DEFAULT_VERSION_WEIGHT = 100;

    /**
     * 默认灰度实例权重
     */
    public static final int DEFAULT_GRAY_INSTANCE_WEIGHT = 10;

    /**
     * 默认稳定实例权重
     */
    public static final int DEFAULT_STABLE_INSTANCE_WEIGHT = 90;

    /**
     * 默认超时时间（毫秒）
     */
    public static final int DEFAULT_TIMEOUT_MILLIS = 3000;

    /**
     * 默认重试次数
     */
    public static final int DEFAULT_RETRY_TIMES = 3;

    /**
     * 默认等待时间（毫秒）
     */
    public static final int DEFAULT_WAIT_MILLIS = 1000;

    // ================== 正则表达式常量 ==================
    /**
     * IP地址正则表达式
     */
    public static final String REGEX_IP = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";

    /**
     * 版本号正则表达式
     */
    public static final String REGEX_VERSION = "^[a-zA-Z0-9._-]+$";

    /**
     * 数字正则表达式
     */
    public static final String REGEX_NUMBER = "^\\d+$";

    /**
     * 用户ID正则表达式
     */
    public static final String REGEX_USER_ID = "^[a-zA-Z0-9_-]{1,50}$";

    // ================== 特殊字符常量 ==================
    /**
     * 分隔符：逗号
     */
    public static final String SEPARATOR_COMMA = ",";

    /**
     * 分隔符：冒号
     */
    public static final String SEPARATOR_COLON = ":";

    /**
     * 分隔符：分号
     */
    public static final String SEPARATOR_SEMICOLON = ";";

    /**
     * 分隔符：竖线
     */
    public static final String SEPARATOR_VERTICAL_BAR = "|";

    /**
     * 分隔符：与符号
     */
    public static final String SEPARATOR_AMPERSAND = "&";

    /**
     * 分隔符：等号
     */
    public static final String SEPARATOR_EQUALS = "=";

    /**
     * 分隔符：问号
     */
    public static final String SEPARATOR_QUESTION = "?";

    /**
     * 分隔符：斜杠
     */
    public static final String SEPARATOR_SLASH = "/";

    /**
     * 分隔符：反斜杠
     */
    public static final String SEPARATOR_BACKSLASH = "\\";

    /**
     * 分隔符：下划线
     */
    public static final String SEPARATOR_UNDERSCORE = "_";

    /**
     * 分隔符：横线
     */
    public static final String SEPARATOR_HYPHEN = "-";

    /**
     * 分隔符：点号
     */
    public static final String SEPARATOR_DOT = ".";

    // ================== 编码常量 ==================
    /**
     * UTF-8编码
     */
    public static final String ENCODING_UTF8 = "UTF-8";

    /**
     * GBK编码
     */
    public static final String ENCODING_GBK = "GBK";

    /**
     * ISO-8859-1编码
     */
    public static final String ENCODING_ISO88591 = "ISO-8859-1";

    // ================== HTTP常量 ==================
    /**
     * HTTP GET方法
     */
    public static final String HTTP_METHOD_GET = "GET";

    /**
     * HTTP POST方法
     */
    public static final String HTTP_METHOD_POST = "POST";

    /**
     * HTTP PUT方法
     */
    public static final String HTTP_METHOD_PUT = "PUT";

    /**
     * HTTP DELETE方法
     */
    public static final String HTTP_METHOD_DELETE = "DELETE";

    /**
     * HTTP PATCH方法
     */
    public static final String HTTP_METHOD_PATCH = "PATCH";

    /**
     * HTTP HEAD方法
     */
    public static final String HTTP_METHOD_HEAD = "HEAD";

    /**
     * HTTP OPTIONS方法
     */
    public static final String HTTP_METHOD_OPTIONS = "OPTIONS";

    /**
     * HTTP成功状态码
     */
    public static final int HTTP_STATUS_SUCCESS = 200;

    /**
     * HTTP创建成功状态码
     */
    public static final int HTTP_STATUS_CREATED = 201;

    /**
     * HTTP无内容状态码
     */
    public static final int HTTP_STATUS_NO_CONTENT = 204;

    /**
     * HTTP错误请求状态码
     */
    public static final int HTTP_STATUS_BAD_REQUEST = 400;

    /**
     * HTTP未授权状态码
     */
    public static final int HTTP_STATUS_UNAUTHORIZED = 401;

    /**
     * HTTP禁止访问状态码
     */
    public static final int HTTP_STATUS_FORBIDDEN = 403;

    /**
     * HTTP未找到状态码
     */
    public static final int HTTP_STATUS_NOT_FOUND = 404;

    /**
     * HTTP方法不允许状态码
     */
    public static final int HTTP_STATUS_METHOD_NOT_ALLOWED = 405;

    /**
     * HTTP请求超时状态码
     */
    public static final int HTTP_STATUS_REQUEST_TIMEOUT = 408;

    /**
     * HTTP服务器错误状态码
     */
    public static final int HTTP_STATUS_INTERNAL_SERVER_ERROR = 500;

    /**
     * HTTP服务不可用状态码
     */
    public static final int HTTP_STATUS_SERVICE_UNAVAILABLE = 503;

    // ================== 日志常量 ==================
    /**
     * 灰度日志记录器名称
     */
    public static final String LOGGER_GRAY = "GRAY_LOGGER";

    /**
     * 灰度追踪日志记录器名称
     */
    public static final String LOGGER_GRAY_TRACE = "GRAY_TRACE";

    /**
     * 灰度监控日志记录器名称
     */
    public static final String LOGGER_GRAY_METRICS = "GRAY_METRICS";

    /**
     * 灰度错误日志记录器名称
     */
    public static final String LOGGER_GRAY_ERROR = "GRAY_ERROR";

    /**
     * 日志分隔线
     */
    public static final String LOG_SEPARATOR = "==========================================";

    /**
     * 日志子分隔线
     */
    public static final String LOG_SUB_SEPARATOR = "------------------------------------------";

    // ================== 配置中心常量 ==================
    /**
     * Nacos配置中心地址
     */
    public static final String NACOS_CONFIG_SERVER_ADDR = "spring.cloud.nacos.config.server-addr";

    /**
     * Nacos配置命名空间
     */
    public static final String NACOS_CONFIG_NAMESPACE = "spring.cloud.nacos.config.namespace";

    /**
     * Nacos配置组
     */
    public static final String NACOS_CONFIG_GROUP = "spring.cloud.nacos.config.group";

    /**
     * Nacos服务发现地址
     */
    public static final String NACOS_DISCOVERY_SERVER_ADDR = "spring.cloud.nacos.discovery.server-addr";

    /**
     * Nacos服务发现命名空间
     */
    public static final String NACOS_DISCOVERY_NAMESPACE = "spring.cloud.nacos.discovery.namespace";

    /**
     * Nacos服务发现组
     */
    public static final String NACOS_DISCOVERY_GROUP = "spring.cloud.nacos.discovery.group";

    // ================== 特性开关常量 ==================
    /**
     * 灰度发布特性开关
     */
    public static final String FEATURE_GRAY_RELEASE = "feature.gray.release";

    /**
     * 灰度路由特性开关
     */
    public static final String FEATURE_GRAY_ROUTE = "feature.gray.route";

    /**
     * 灰度负载均衡特性开关
     */
    public static final String FEATURE_GRAY_LOADBALANCER = "feature.gray.loadbalancer";

    /**
     * 灰度监控特性开关
     */
    public static final String FEATURE_GRAY_MONITORING = "feature.gray.monitoring";

    /**
     * 灰度配置热更新特性开关
     */
    public static final String FEATURE_GRAY_CONFIG_HOT_RELOAD = "feature.gray.config.hot.reload";

    // ================== 工具类常量 ==================
    /**
     * 空字符串
     */
    public static final String EMPTY_STRING = "";

    /**
     * 空格字符串
     */
    public static final String SPACE_STRING = " ";

    /**
     * 换行符
     */
    public static final String NEW_LINE = System.lineSeparator();

    /**
     * Tab缩进
     */
    public static final String TAB_INDENT = "\t";

    /**
     * 是
     */
    public static final String YES = "Y";

    /**
     * 否
     */
    public static final String NO = "N";

    /**
     * 真
     */
    public static final String TRUE = "true";

    /**
     * 假
     */
    public static final String FALSE = "false";

    // ================== 构造函数 ==================
    /**
     * 私有构造函数，防止实例化
     */
    private GrayConstant() {
        throw new UnsupportedOperationException("GrayConstant is a utility class and cannot be instantiated");
    }

    // ================== 静态工具方法 ==================
    /**
     * 判断是否为灰度版本
     * @param version 版本号
     * @return 是否为灰度版本
     */
    public static boolean isGrayVersion(String version) {
        return GRAY_VERSION.equalsIgnoreCase(version) ||
                (version != null && version.toLowerCase().contains("gray"));
    }

    /**
     * 判断是否为稳定版本
     * @param version 版本号
     * @return 是否为稳定版本
     */
    public static boolean isStableVersion(String version) {
        return STABLE_VERSION.equalsIgnoreCase(version) ||
                !isGrayVersion(version);
    }

    /**
     * 获取灰度标记值
     * @param isGray 是否为灰度请求
     * @return 灰度标记值
     */
    public static String getGrayTag(boolean isGray) {
        return isGray ? GRAY_VERSION : STABLE_VERSION;
    }

    /**
     * 根据灰度标记判断是否为灰度请求
     * @param grayTag 灰度标记
     * @return 是否为灰度请求
     */
    public static boolean isGrayRequest(String grayTag) {
        return GRAY_VERSION.equalsIgnoreCase(grayTag);
    }

    /**
     * 获取版本权重
     * @param version 版本号
     * @param grayWeight 灰度权重
     * @param stableWeight 稳定权重
     * @return 权重值
     */
    public static int getVersionWeight(String version, int grayWeight, int stableWeight) {
        return isGrayVersion(version) ? grayWeight : stableWeight;
    }
}
