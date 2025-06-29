package ynu.edu.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * JMeter测试专用控制器
 * 用于测试各种容错机制的效果
 */
@RestController
@RequestMapping("/jmeter")
public class JmeterTestController {

    private static final Logger log = LoggerFactory.getLogger(JmeterTestController.class);

    // 用于记录访问次数的计数器
    private final AtomicInteger counter = new AtomicInteger(0);

    // 专用计数器
    private final AtomicInteger circuitACounter = new AtomicInteger(0);
    private final AtomicInteger circuitBCounter = new AtomicInteger(0);
    private final AtomicInteger bulkheadCounter = new AtomicInteger(0);
    private final AtomicInteger ratelimiterCounter = new AtomicInteger(0);

    // 用于跟踪重试次数
    private final ThreadLocal<Integer> retryCount = ThreadLocal.withInitial(() -> 0);

    /**
     * 断路器A计数方法
     */
    public void incrementCircuitA() {
        log.info("断路器A计数增加");
        circuitACounter.incrementAndGet();
    }

    /**
     * 断路器B计数方法
     */
    public void incrementCircuitB() {
        log.info("断路器B计数增加");
        circuitBCounter.incrementAndGet();
    }

    /**
     * 隔离器计数方法
     */
    public void incrementBulkhead() {
        log.info("隔离器计数增加");
        bulkheadCounter.incrementAndGet();
    }

    /**
     * 限流器计数方法
     */
    public void incrementRatelimiter() {
        log.info("限流器计数增加");
        ratelimiterCounter.incrementAndGet();
    }

    /**
     * 查看所有计数器状态
     */
    @GetMapping("/counters")
    public Map<String, Integer> getCounters() {
        Map<String, Integer> counters = new HashMap<>();
        counters.put("general", counter.get());
        counters.put("circuitA", circuitACounter.get());
        counters.put("circuitB", circuitBCounter.get());
        counters.put("bulkhead", bulkheadCounter.get());
        counters.put("ratelimiter", ratelimiterCounter.get());
        return counters;
    }

    /**
     * 热点参数限流测试
     * 通过ID参数控制是否触发限流
     */
    @GetMapping("/hotspot/{id}")
    @RateLimiter(name = "hotParameterRateLimiter", fallbackMethod = "hotspotFallback")
    public Map<String, Object> testHotspot(@PathVariable("id") Integer id) {
        log.info("热点参数测试, id = {}, 计数 = {}", id, counter.incrementAndGet());

        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("count", counter.get());
        result.put("status", "success");
        result.put("message", "热点参数请求成功处理");

        // ID=1为热点参数，增加处理延迟
        if (id == 1) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            result.put("type", "hotspot");
        }

        return result;
    }

    /**
     * 热点参数限流降级方法
     */
    public Map<String, Object> hotspotFallback(Integer id, Exception e) {
        log.warn("热点参数限流降级, id = {}, 错误: {}", id, e.getMessage());

        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("status", "limited");
        result.put("message", "请求被限流: " + e.getMessage());
        return result;
    }

    /**
     * 缓存测试
     * 相同ID的请求会使用缓存，降低服务器负载
     */
    @GetMapping("/cache/{id}")
    @Cacheable(value = "jmeterCache", key = "#id")
    public Map<String, Object> testCache(@PathVariable("id") Integer id) {
        int currentCount = counter.incrementAndGet();
        log.info("缓存测试, id = {}, 计数 = {}", id, currentCount);

        // 模拟处理耗时
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("count", currentCount);
        result.put("timestamp", System.currentTimeMillis());
        result.put("message", "此结果将被缓存60秒");

        return result;
    }

    /**
     * 超时测试
     * 可以通过delay参数控制处理时间是否超过超时限制
     */
    @GetMapping("/timeout")
    @TimeLimiter(name = "timeoutLimiter", fallbackMethod = "timeoutFallback")
    public CompletableFuture<Map<String, Object>> testTimeout(@RequestParam(defaultValue = "0") int delay) {
        return CompletableFuture.supplyAsync(() -> {
            int count = counter.incrementAndGet();
            log.info("超时测试, delay = {}ms, 计数 = {}", delay, count);

            // 根据参数决定是否会超时
            if (delay > 0) {
                try {
                    log.info("执行延迟操作: {}ms", delay);
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("delay", delay);
            result.put("count", count);
            result.put("status", "success");
            result.put("message", "请求成功完成，无超时");

            return result;
        });
    }

    /**
     * 超时降级方法
     */
    public CompletableFuture<Map<String, Object>> timeoutFallback(int delay, Exception e) {
        log.warn("超时降级触发, delay = {}ms, 错误: {}", delay, e.getMessage());

        Map<String, Object> result = new HashMap<>();
        result.put("delay", delay);
        result.put("status", "timeout");
        result.put("message", "请求处理超时: " + e.getMessage());

        return CompletableFuture.completedFuture(result);
    }

    /**
     * 重试测试
     * 通过fail参数控制是否触发失败重试
     */
    @GetMapping("/retry")
    @Retry(name = "retryService", fallbackMethod = "retryFallback")
    public Map<String, Object> testRetry(@RequestParam(defaultValue = "false") boolean fail) {
        int attempt = getRetryCount();
        int count = counter.incrementAndGet();

        log.info("重试测试, fail = {}, 尝试次数 = {}, 计数 = {}", fail, attempt, count);

        if (fail && attempt < 2) { // 前两次失败，第三次成功
            log.info("触发失败，将重试");
            throw new RuntimeException("模拟服务暂时不可用 (尝试 #" + attempt + ")");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("fail", fail);
        result.put("attempt", attempt);
        result.put("count", count);
        result.put("status", "success");
        result.put("message", fail ? "重试后成功" : "首次成功");

        // 重置重试计数
        retryCount.remove();
        return result;
    }

    private int getRetryCount() {
        int count = retryCount.get();
        retryCount.set(count + 1);
        return count;
    }

    /**
     * 重试降级方法
     */
    public Map<String, Object> retryFallback(boolean fail, Exception e) {
        log.warn("重试降级触发, fail = {}, 错误: {}", fail, e.getMessage());

        // 重置重试计数
        retryCount.remove();

        Map<String, Object> result = new HashMap<>();
        result.put("fail", fail);
        result.put("status", "failed");
        result.put("message", "重试耗尽后仍然失败: " + e.getMessage());

        return result;
    }

    /**
     * 清除计数器
     */
    @PostMapping("/reset")
    public Map<String, Object> resetCounter() {
        int oldCount = counter.getAndSet(0);
        circuitACounter.set(0);
        circuitBCounter.set(0);
        bulkheadCounter.set(0);
        ratelimiterCounter.set(0);
        log.info("重置所有计数器，通用计数器原值 = {}", oldCount);

        Map<String, Object> result = new HashMap<>();
        result.put("oldCount", oldCount);
        result.put("newCount", 0);
        result.put("status", "success");
        result.put("message", "所有计数器已重置");

        return result;
    }
}