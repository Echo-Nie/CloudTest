package ynu.edu.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import ynu.edu.entity.User;
import ynu.edu.feign.UserFeignClient;

import java.util.concurrent.CompletableFuture;

/**
 * 高级容错功能控制器
 * 实现热点参数限流、缓存和超时处理等高级容错功能
 */
@RestController
@RequestMapping("/advanced")
public class AdvancedResilience4jController {

    private static final Logger log = LoggerFactory.getLogger(AdvancedResilience4jController.class);

    @Resource
    private UserFeignClient userFeignClient;

    /**
     * 热点参数限流 - 针对特定参数值进行限流
     * 对于热门ID（例如ID=1），限制其访问频率
     */
    @GetMapping("/hotParam/{id}")
    @RateLimiter(name = "hotParameterRateLimiter", fallbackMethod = "hotParamFallback")
    public User getUserWithHotParamLimit(@PathVariable("id") Integer id) {
        log.info("热点参数访问, id = {}", id);

        // 热点ID特殊处理（例如ID=1是热门商品）
        if (id == 1) {
            log.info("访问热点参数ID=1, 可能触发限流");
            // 这里可以做一些特殊处理，如增加延迟来模拟热点数据处理的复杂性
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        try {
            return userFeignClient.getUserById(id);
        } catch (Exception e) {
            log.error("热点参数调用出错: {}", e.getMessage());
            throw e; // 这里抛出异常以触发fallback
        }
    }

    /**
     * 热点参数限流的降级方法
     */
    public User hotParamFallback(Integer id, Exception e) {
        log.warn("热点参数限流, id = {}, 错误: {}", id, e.getMessage());
        User fallbackUser = new User();
        fallbackUser.setUserId(id);
        fallbackUser.setUserName("热点参数限流降级-" + e.getMessage());
        fallbackUser.setPassWord("");
        fallbackUser.setAddress("热点参数限流降级服务");
        return fallbackUser;
    }

    /**
     * 结果缓存 - 使用Spring Cache缓存查询结果
     * 按ID缓存用户信息，减少远程调用
     */
    @GetMapping("/cache/{id}")
    @Cacheable(value = "userCache", key = "#id")
    public User getUserWithCache(@PathVariable("id") Integer id) {
        log.info("缓存方法调用, id = {}", id);

        try {
            // 模拟一些延迟，让缓存效果更明显
            try {
                log.info("正在从远程获取数据，通常这很耗时...");
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return userFeignClient.getUserById(id);
        } catch (Exception e) {
            log.error("缓存方法调用出错: {}", e.getMessage(), e);
            User fallbackUser = new User();
            fallbackUser.setUserId(id);
            fallbackUser.setUserName("缓存调用错误-" + e.getMessage());
            fallbackUser.setPassWord("");
            fallbackUser.setAddress("缓存错误降级服务");
            return fallbackUser;
        }
    }

    /**
     * 提供一个不使用缓存的测试接口，用于排查问题
     */
    @GetMapping("/test/{id}")
    public User testUserService(@PathVariable("id") Integer id) {
        try {
            return userFeignClient.getUserById(id);
        } catch (Exception e) {
            log.error("测试接口调用出错: {}", e.getMessage(), e);
            User fallbackUser = new User();
            fallbackUser.setUserId(id);
            fallbackUser.setUserName("测试接口错误-" + e.getMessage());
            fallbackUser.setPassWord("");
            fallbackUser.setAddress("测试接口降级服务");
            return fallbackUser;
        }
    }

    /**
     * 超时处理 - 设置调用超时时间，防止长时间阻塞
     */
    @GetMapping("/timeout/{id}")
    @TimeLimiter(name = "timeoutLimiter", fallbackMethod = "timeoutFallback")
    public CompletableFuture<User> getUserWithTimeout(@PathVariable("id") Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("超时保护方法调用, id = {}", id);

            // 模拟耗时操作，根据ID决定是否会超时
            if (id == 0) {
                try {
                    log.info("执行一个超时操作...");
                    Thread.sleep(2000); // 故意超过1秒配置的超时时间
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            try {
                return userFeignClient.getUserById(id);
            } catch (Exception e) {
                log.error("超时保护方法调用出错: {}", e.getMessage());
                throw e; // 抛出异常以触发fallback
            }
        });
    }

    /**
     * 超时处理的降级方法
     */
    public CompletableFuture<User> timeoutFallback(Integer id, Exception e) {
        log.warn("超时保护触发降级, id = {}, 错误: {}", id, e.getMessage());
        User fallbackUser = new User();
        fallbackUser.setUserId(id);
        fallbackUser.setUserName("超时降级-" + e.getMessage());
        fallbackUser.setPassWord("");
        fallbackUser.setAddress("超时降级服务");
        return CompletableFuture.completedFuture(fallbackUser);
    }

    /**
     * 重试机制 - 当出现特定异常时进行重试
     */
    @GetMapping("/retry/{id}")
    @Retry(name = "retryService", fallbackMethod = "retryFallback")
    public User getUserWithRetry(@PathVariable("id") Integer id) {
        log.info("重试保护方法调用, id = {}, 尝试次数: {}", id, getRetryCount());

        // 模拟随机失败，来触发重试机制
        if (id < 0 || Math.random() < 0.5) {
            log.error("服务暂时不可用，将触发重试");
            throw new RuntimeException("模拟的临时服务故障");
        }

        try {
            return userFeignClient.getUserById(id);
        } catch (Exception e) {
            log.error("重试保护方法调用出错: {}", e.getMessage());
            throw e; // 抛出异常以触发重试和fallback
        }
    }

    // 用于跟踪重试次数
    private final ThreadLocal<Integer> retryCount = ThreadLocal.withInitial(() -> 0);

    private int getRetryCount() {
        int count = retryCount.get();
        retryCount.set(count + 1);
        return count;
    }

    /**
     * 重试机制的降级方法
     */
    public User retryFallback(Integer id, Exception e) {
        log.warn("重试机制触发降级, id = {}, 错误: {}", id, e.getMessage());
        // 重置重试计数
        retryCount.remove();

        User fallbackUser = new User();
        fallbackUser.setUserId(id);
        fallbackUser.setUserName("重试降级-" + e.getMessage());
        fallbackUser.setPassWord("");
        fallbackUser.setAddress("重试降级服务");
        return fallbackUser;
    }

    /**
     * 组合使用多种容错机制
     * 包括：限流 + 缓存 + 超时控制
     */
    @GetMapping("/combined/{id}")
    @RateLimiter(name = "userServiceRateLimiter")
    @Cacheable(value = "userCache", key = "#id")
    @TimeLimiter(name = "timeoutLimiter")
    public CompletableFuture<User> getUserWithCombinedProtection(@PathVariable("id") Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("组合保护方法调用, id = {}", id);
            try {
                return userFeignClient.getUserById(id);
            } catch (Exception e) {
                log.error("组合保护方法调用出错: {}", e.getMessage());
                User fallbackUser = new User();
                fallbackUser.setUserId(id);
                fallbackUser.setUserName("组合保护错误降级-" + e.getMessage());
                fallbackUser.setPassWord("");
                fallbackUser.setAddress("组合保护错误降级服务");
                return fallbackUser;
            }
        });
    }
}