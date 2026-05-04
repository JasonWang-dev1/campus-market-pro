package com.campusmarket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI 客户端统一配置
 */
@Data
@ConfigurationProperties(prefix = "ai")
public class AIClientProperties {

    /** 提供商: mimo / deepseek / ollama */
    private String provider = "mimo";

    /** API Key（Ollama 本地模型可为空） */
    private String apiKey;

    /** API 基础地址 */
    private String baseUrl = "https://token-plan-cn.xiaomimimo.com/v1";

    /** 模型名称 */
    private String model = "MiMo-V2.5-Pro";

    /** 温度参数 */
    private double temperature = 0.1;

    /** 最大 token 数 */
    private int maxTokens = 4096;

    /** 连接超时（秒） */
    private int connectTimeout = 30;

    /** 读取超时（秒） */
    private int readTimeout = 120;

    /** 重试配置 */
    private Retry retry = new Retry();

    @Data
    public static class Retry {
        /** 最大重试次数 */
        private int maxAttempts = 3;

        /** 重试退避基础时间（毫秒） */
        private long backoffMs = 1000;
    }
}
