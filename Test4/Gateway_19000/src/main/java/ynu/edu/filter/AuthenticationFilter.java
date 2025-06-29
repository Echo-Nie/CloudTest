package ynu.edu.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 全局认证过滤器
 * 实现简单的token验证功能
 */
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

    // 定义不需要验证TOKEN的路径
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/provider/user/health",
            "/consumer/health",
            "/nacos-provider/health",
            "/nacos-consumer/health",
            "/provider-direct/user/list", // 添加直连路径到白名单
            "/provider-direct/" // 添加provider-direct前缀路径
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 日志记录请求路径
        log.info("Gateway认证过滤器接收到请求: {}", path);

        // 白名单路径直接放行
        for (String whitePath : WHITE_LIST) {
            if (path.contains(whitePath)) {
                log.info("白名单路径: {}, 直接放行", path);
                return chain.filter(exchange);
            }
        }

        // 获取token
        String token = request.getHeaders().getFirst("Authorization");

        // 简单验证token (实际项目中应该有更复杂的验证逻辑)
        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("未授权请求，缺少有效的token");
            return unauthorizedResponse(exchange);
        }

        // 这里可以添加更多token验证逻辑，如JWT验证等

        log.info("认证通过: {}", path);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 过滤器顺序，数值越小优先级越高
        return 0;
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        String message = "{\"code\": 401, \"message\": \"未授权访问\"}";
        DataBuffer buffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }
}