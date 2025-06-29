package ynu.edu.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置类，用于配置缓存管理器和缓存策略
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

    /**
     * 配置Caffeine缓存管理器
     * 
     * @return CacheManager 缓存管理器实例
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 设置最大缓存条目数为1000
                .maximumSize(1000)
                // 设置写入后过期时间为60秒
                .expireAfterWrite(60, TimeUnit.SECONDS)
                // 设置访问后过期时间为30秒
                .expireAfterAccess(30, TimeUnit.SECONDS)
                // 开启缓存统计功能
                .recordStats());

        // 允许缓存null值，避免缓存穿透和错误传播
        cacheManager.setAllowNullValues(true);

        // 设置默认缓存名称
        cacheManager.setCacheNames(java.util.Arrays.asList("userCache", "jmeterCache"));

        return cacheManager;
    }
}