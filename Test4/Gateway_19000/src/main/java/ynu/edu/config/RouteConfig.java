package ynu.edu.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import ynu.edu.handler.FallbackHandler;

/**
 * 代码方式的路由配置
 * 包含熔断功能
 */
@Configuration
public class RouteConfig {

        private final FallbackHandler fallbackHandler;
        private final KeyResolver ipKeyResolver;

        public RouteConfig(FallbackHandler fallbackHandler,
                        @Qualifier("ipKeyResolver") KeyResolver ipKeyResolver) {
                this.fallbackHandler = fallbackHandler;
                this.ipKeyResolver = ipKeyResolver;
        }

        @Bean
        public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
                return builder.routes()
                                // 带熔断的服务提供者路由
                                .route("provider_circuit_breaker_route", r -> r
                                                .path("/provider-cb/**")
                                                .filters(f -> f
                                                                .stripPrefix(1)
                                                                .circuitBreaker(config -> config
                                                                                .setName("providerCircuitBreaker")
                                                                                .setFallbackUri("forward:/fallback"))
                                                                .retry(retryConfig -> retryConfig
                                                                                .setRetries(3)
                                                                                .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR)))
                                                .uri("lb://PROVIDER-SERVICE"))

                                // 降级路由
                                .route("fallback_route", r -> r
                                                .path("/fallback")
                                                .uri("forward:/fallback"))

                                .build();
        }
}