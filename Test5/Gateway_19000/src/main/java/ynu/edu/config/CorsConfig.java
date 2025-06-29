package ynu.edu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

import java.time.Duration;
import java.util.Arrays;

/**
 * 全局跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许的域名
        config.addAllowedOriginPattern("*");  // 允许所有域名，生产环境应该指定具体域名
        
        // 允许的请求头
        config.addAllowedHeader("*");
        
        // 允许的请求方法
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 允许cookies
        config.setAllowCredentials(true);
        
        // 预检请求的缓存时间
        config.setMaxAge(Duration.ofSeconds(3600));
        
        // 暴露的响应头
        config.addExposedHeader("Authorization");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);
        
        return new CorsWebFilter(source);
    }
} 