package com.wtb.livesystem.streamer.config.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingFilterConfig {

    @Bean
    public CommonsRequestLoggingFilter loggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);    // 记录查询参数
        filter.setIncludePayload(true);        // 记录请求体
        filter.setMaxPayloadLength(10000);     // 请求体最大长度
        filter.setIncludeHeaders(true);        // 记录请求头
        filter.setAfterMessagePrefix("REQUEST DATA: ");
        return filter;
    }
}