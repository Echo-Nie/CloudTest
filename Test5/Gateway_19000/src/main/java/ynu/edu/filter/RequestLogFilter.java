package ynu.edu.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 请求日志过滤器
 * 记录请求信息用于审计和调试
 */
@Component
public class RequestLogFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(RequestLogFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        String methodName = request.getMethod().toString();

        // 记录请求起始时间
        long startTime = System.currentTimeMillis();

        // 记录请求头部
        List<String> headers = new ArrayList<>();
        request.getHeaders().forEach((name, values) -> {
            values.forEach(value -> headers.add(name + ": " + value));
        });

        log.info("Gateway received request: {} {} with headers: {}",
                methodName, uri.getPath(), headers);

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // 记录请求结束时间和处理时长
            long endTime = System.currentTimeMillis();
            log.info("Request {} {} completed in {} ms with status: {}",
                    methodName, uri.getPath(), (endTime - startTime),
                    exchange.getResponse().getStatusCode());
        }));
    }

    @Override
    public int getOrder() {
        // 该过滤器在认证过滤器之前执行
        return -1;
    }
}