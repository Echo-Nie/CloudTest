package ynu.edu.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {

    /**
     * 全局默认配置
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(4))
                        .build())
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(5))
                        .slidingWindowSize(10)
                        .minimumNumberOfCalls(5)
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .build())
                .build());
    }

    /**
     * 配置断路器实例A
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> circuitBreakerACustomizer() {
        return factory -> factory.configure(builder -> builder
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .failureRateThreshold(30) // 失败率阈值为30%
                        .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED) // 滑动窗口类型为时间窗口型
                        .slidingWindowSize(10) // 滑动窗口长度为10秒
                        .minimumNumberOfCalls(5) // 滑动窗口内最小请求个数为5
                        .waitDurationInOpenState(Duration.ofSeconds(5)) // 自动从OPEN状态变为HALF_OPEN状态的等待时间为5秒
                        .permittedNumberOfCallsInHalfOpenState(3) // HALF_OPEN状态时允许测试响应能力的请求数为3
                        .automaticTransitionFromOpenToHalfOpenEnabled(true) // 允许自动从OPEN到HALF_OPEN的转换
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(4))
                        .build()),
                "circuitBreakerA");
    }

    /**
     * 配置断路器实例B
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> circuitBreakerBCustomizer() {
        return factory -> factory.configure(builder -> builder
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .failureRateThreshold(50) // 失败率阈值为50%
                        .slowCallRateThreshold(30) // 慢调用阈值为30%
                        .slowCallDurationThreshold(Duration.ofSeconds(2)) // 慢调用时间阈值为2秒
                        .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED) // 滑动窗口类型为时间窗口型
                        .slidingWindowSize(10) // 滑动窗口长度为10秒
                        .minimumNumberOfCalls(5) // 滑动窗口内最小请求个数为5
                        .waitDurationInOpenState(Duration.ofSeconds(5)) // 自动从OPEN状态变为HALF_OPEN状态的等待时间为5秒
                        .permittedNumberOfCallsInHalfOpenState(3) // HALF_OPEN状态时允许测试响应能力的请求数为3
                        .automaticTransitionFromOpenToHalfOpenEnabled(true) // 允许自动从OPEN到HALF_OPEN的转换
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(4))
                        .build()),
                "circuitBreakerB");
    }
}