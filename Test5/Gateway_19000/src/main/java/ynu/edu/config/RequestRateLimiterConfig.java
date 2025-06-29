package ynu.edu.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * 限流配置类
 */
@Configuration
public class RequestRateLimiterConfig {

    /**
     * 按照Path限流
     */
    @Bean
    public KeyResolver pathKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getURI().getPath());
    }

    /**
     * 按照IP地址限流
     */
    @Bean
    @Primary
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getRemoteAddress().getHostString();
            return Mono.just(ip);
        };
    }
}