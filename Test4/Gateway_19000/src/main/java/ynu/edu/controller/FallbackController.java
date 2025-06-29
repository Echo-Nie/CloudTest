package ynu.edu.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ynu.edu.handler.FallbackHandler;

/**
 * 降级处理控制器
 */
@RestController
public class FallbackController {
    
    private final FallbackHandler fallbackHandler;
    
    public FallbackController(FallbackHandler fallbackHandler) {
        this.fallbackHandler = fallbackHandler;
    }
    
    @RequestMapping("/fallback")
    public Mono<String> fallback(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        return Mono.just("服务暂时不可用，请稍后再试。请求路径: " + path);
    }
} 