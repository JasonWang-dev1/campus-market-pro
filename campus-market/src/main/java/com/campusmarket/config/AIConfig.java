package com.campusmarket.config;

import com.campusmarket.client.AIClient;
import com.campusmarket.client.OpenAICompatibleAIClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 客户端自动配置
 * <p>
 * 根据 ai.provider 配置创建对应的 AIClient 实例。
 * </p>
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(AIClientProperties.class)
public class AIConfig {

    @Bean
    public AIClient aiClient(AIClientProperties props) {
        log.info("正在创建 AIClient: provider={}, baseUrl={}, model={}",
                props.getProvider(), props.getBaseUrl(), props.getModel());
        return new OpenAICompatibleAIClient(props);
    }
}
