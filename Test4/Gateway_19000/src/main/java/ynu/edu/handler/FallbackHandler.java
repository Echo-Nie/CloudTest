package ynu.edu.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务降级处理器
 * 处理网关调用后端服务失败的情况
 */
@Component
public class FallbackHandler {
    
    private static final Logger log = LoggerFactory.getLogger(FallbackHandler.class);
    
    public Mono<ServerResponse> handleFallback(ServerRequest request) {
        log.error("Gateway fallback: {} - {}", request.path(), request.uri());
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
        result.put("message", "服务暂时不可用，请稍后再试");
        result.put("path", request.path());
        result.put("uri", request.uri().toString());
        
        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(result));
    }
} 