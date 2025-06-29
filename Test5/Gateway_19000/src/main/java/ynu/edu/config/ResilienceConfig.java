package ynu.edu.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Resilience4j熔断降级配置
 */
@Configuration
public class ResilienceConfig {

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(10) // 滑动窗口大小
                        .failureRateThreshold(50) // 失败率阈值
                        .waitDurationInOpenState(Duration.ofSeconds(10)) // 断路器打开状态持续时间
                        .permittedNumberOfCallsInHalfOpenState(5) // 半开状态下允许的调用次数
                        .slowCallRateThreshold(50) // 慢调用比例阈值
                        .slowCallDurationThreshold(Duration.ofSeconds(2)) // 慢调用时长阈值
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(3)) // 超时时间
                        .build())
                .build());
    }
} 