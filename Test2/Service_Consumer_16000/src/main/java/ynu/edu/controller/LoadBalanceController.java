package ynu.edu.controller;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ynu.edu.feign.UserFeignClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 负载均衡测试控制器
 */
@RestController
@RequestMapping("/lb-test")
public class LoadBalanceController {
    private static final Logger log = LoggerFactory.getLogger(LoadBalanceController.class);

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private UserFeignClient userFeignClient;

    private static final String SERVICE_URL = "http://provider-service/user";

    /**
     * 测试RestTemplate默认轮询负载均衡
     * 
     * @param count 请求次数
     * @return 测试结果
     */
    @GetMapping("/round-robin")
    public Map<String, Object> testRoundRobin(@RequestParam(defaultValue = "10") int count) {
        log.info("开始测试轮询负载均衡，请求次数: {}", count);

        List<String> responses = new ArrayList<>();
        Map<String, Integer> statistics = new HashMap<>();

        for (int i = 0; i < count; i++) {
            String response = restTemplate.getForObject(SERVICE_URL + "/health", String.class);
            responses.add(response);
            statistics.put(response, statistics.getOrDefault(response, 0) + 1);
            log.info("请求 #{}: {}", i + 1, response);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("responses", responses);
        result.put("statistics", statistics);

        return result;
    }

    /**
     * 测试Feign客户端负载均衡
     * 
     * @param count 请求次数
     * @return 测试结果
     */
    @GetMapping("/feign")
    public Map<String, Object> testFeignLoadBalancer(@RequestParam(defaultValue = "10") int count) {
        log.info("开始测试Feign负载均衡，请求次数: {}", count);

        List<String> responses = new ArrayList<>();
        Map<String, Integer> statistics = new HashMap<>();

        for (int i = 0; i < count; i++) {
            String response = userFeignClient.checkHealth();
            responses.add(response);
            statistics.put(response, statistics.getOrDefault(response, 0) + 1);
            log.info("请求 #{}: {}", i + 1, response);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("responses", responses);
        result.put("statistics", statistics);

        return result;
    }

    /**
     * 测试自定义的三次调用负载均衡策略
     * 
     * @param count 请求次数
     * @return 测试结果
     */
    @GetMapping("/three-times")
    public Map<String, Object> testThreeTimesLoadBalancer(@RequestParam(defaultValue = "10") int count) {
        log.info("开始测试三次调用负载均衡策略，请求次数: {}", count);

        List<String> responses = new ArrayList<>();
        Map<String, Integer> statistics = new HashMap<>();

        for (int i = 0; i < count; i++) {
            // 使用RestTemplate进行调用，会使用ConsumerApplication16000中配置的ThreeTimeLoadBalancer
            String response = restTemplate.getForObject(SERVICE_URL + "/health", String.class);
            responses.add(response);
            statistics.put(response, statistics.getOrDefault(response, 0) + 1);
            log.info("请求 #{}: {}", i + 1, response);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("responses", responses);
        result.put("statistics", statistics);
        result.put("note", "使用ThreeTimeLoadBalancer策略，每个实例连续调用3次后切换");

        return result;
    }
}