package ynu.edu.controller;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ynu.edu.entity.User;
import ynu.edu.feign.ServiceProviderService;
import ynu.edu.feign.UserFeignClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/resilience")
public class Resilience4jController {

    private static final Logger log = LoggerFactory.getLogger(Resilience4jController.class);

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private ServiceProviderService serviceProviderService;

    @Resource
    private JmeterTestController jmeterTestController;

    /**
     * 使用断路器A进行保护的接口
     */
    @GetMapping("/circuitA/{id}")
    @CircuitBreaker(name = "circuitBreakerA", fallbackMethod = "circuitAFallback")
    public User getUserWithCircuitBreakerA(@PathVariable("id") Integer id) {
        // 记录请求次数，用于JMeter测试
        jmeterTestController.incrementCircuitA();
        log.info("Circuit A received request with id: {}", id);

        // 如果id为负数，模拟调用失败
        if (id < 0) {
            log.error("Circuit A error: userId cannot be negative, id: {}", id);
            throw new RuntimeException("用户ID不能为负数");
        }

        // 如果id为0，模拟服务超时
        if (id == 0) {
            log.warn("Circuit A slow call simulation for id: 0");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // 正常调用
        log.info("Circuit A making normal service call for id: {}", id);
        return serviceProviderService.GetUserById(id);
    }

    /**
     * 断路器A的降级方法
     */
    public User circuitAFallback(Integer id, Exception e) {
        log.warn("Circuit A fallback executed for id: {}, error: {}", id, e.getMessage());
        User fallbackUser = new User();
        fallbackUser.setUserId(id);
        fallbackUser.setUserName("CircuitBreakerA降级-" + e.getMessage());
        fallbackUser.setPassWord("");
        fallbackUser.setAddress("CircuitBreakerA降级服务");
        return fallbackUser;
    }

    /**
     * 使用断路器B进行保护的接口
     */
    @GetMapping("/circuitB/{id}")
    @CircuitBreaker(name = "circuitBreakerB", fallbackMethod = "circuitBFallback")
    public User getUserWithCircuitBreakerB(@PathVariable("id") Integer id) {
        // 记录请求次数，用于JMeter测试
        jmeterTestController.incrementCircuitB();
        log.info("Circuit B received request with id: {}", id);

        // 如果id为负数，模拟调用失败
        if (id < 0) {
            log.error("Circuit B error: userId cannot be negative, id: {}", id);
            throw new RuntimeException("用户ID不能为负数");
        }

        // 如果id为0，模拟慢调用
        if (id == 0) {
            log.warn("Circuit B slow call simulation for id: 0");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // 正常调用
        log.info("Circuit B making normal service call for id: {}", id);
        return userFeignClient.getUserById(id);
    }

    /**
     * 断路器B的降级方法
     */
    public User circuitBFallback(Integer id, Exception e) {
        log.warn("Circuit B fallback executed for id: {}, error: {}", id, e.getMessage());
        User fallbackUser = new User();
        fallbackUser.setUserId(id);
        fallbackUser.setUserName("CircuitBreakerB降级-" + e.getMessage());
        fallbackUser.setPassWord("");
        fallbackUser.setAddress("CircuitBreakerB降级服务");
        return fallbackUser;
    }

    /**
     * 使用隔离器进行保护的接口
     */
    @GetMapping("/bulkhead/users")
    @Bulkhead(name = "userServiceBulkhead", fallbackMethod = "bulkheadFallback")
    public List<User> getUsersWithBulkhead() {
        // 记录请求次数，用于JMeter测试
        jmeterTestController.incrementBulkhead();
        log.info("Bulkhead received request");

        // 模拟耗时操作
        try {
            log.info("Bulkhead processing request, sleeping for 100ms");
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("Bulkhead making normal service call");
        return userFeignClient.getAllUsers();
    }

    /**
     * 隔离器的降级方法
     */
    public List<User> bulkheadFallback(Exception e) {
        log.warn("Bulkhead fallback executed, error: {}", e.getMessage());
        User fallbackUser = new User();
        fallbackUser.setUserId(0);
        fallbackUser.setUserName("Bulkhead降级-" + e.getMessage());
        fallbackUser.setPassWord("");
        fallbackUser.setAddress("Bulkhead降级服务");
        return List.of(fallbackUser);
    }

    /**
     * 使用限流器进行保护的接口
     */
    @GetMapping("/ratelimiter/{id}")
    @RateLimiter(name = "userServiceRateLimiter", fallbackMethod = "rateLimiterFallback")
    public User getUserWithRateLimiter(@PathVariable("id") Integer id) {
        // 记录请求次数，用于JMeter测试
        jmeterTestController.incrementRatelimiter();
        log.info("RateLimiter received request for id: {}", id);
        return userFeignClient.getUserById(id);
    }

    /**
     * 限流器的降级方法
     */
    public User rateLimiterFallback(Integer id, Exception e) {
        log.warn("RateLimiter fallback executed for id: {}, error: {}", id, e.getMessage());
        User fallbackUser = new User();
        fallbackUser.setUserId(id);
        fallbackUser.setUserName("RateLimiter降级-" + e.getMessage());
        fallbackUser.setPassWord("");
        fallbackUser.setAddress("RateLimiter降级服务");
        return fallbackUser;
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public String health() {
        return "Resilience4j Controller is working";
    }
}