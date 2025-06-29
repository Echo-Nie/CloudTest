package ynu.edu.controller;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 负载均衡诊断控制器
 */
@RestController
@RequestMapping("/debug")
public class LoadBalanceDebugController {
    private static final Logger log = LoggerFactory.getLogger(LoadBalanceDebugController.class);

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private DiscoveryClient discoveryClient;

    /**
     * 检查注册的服务列表
     */
    @GetMapping("/services")
    public Map<String, Object> checkServices() {
        Map<String, Object> result = new HashMap<>();

        // 获取所有服务名
        List<String> services = discoveryClient.getServices();
        result.put("availableServices", services);

        // 获取provider-service的实例
        List<ServiceInstance> instances = discoveryClient.getInstances("provider-service");
        result.put("providerInstances", instances);
        result.put("instanceCount", instances.size());

        // 检查每个实例
        for (ServiceInstance instance : instances) {
            log.info("发现服务实例: {}, host={}, port={}",
                    instance.getServiceId(),
                    instance.getHost(),
                    instance.getPort());
        }

        return result;
    }

    /**
     * 直接调用健康检查API进行测试
     */
    @GetMapping("/direct-test")
    public Map<String, Object> directTest() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 直接调用11000端口
            String response11000 = restTemplate.getForObject(
                    "http://localhost:11000/user/health", String.class);
            result.put("11000_response", response11000);

            // 直接调用11001端口
            String response11001 = restTemplate.getForObject(
                    "http://localhost:11001/user/health", String.class);
            result.put("11001_response", response11001);

        } catch (Exception e) {
            result.put("error", e.getMessage());
        }

        return result;
    }

    /**
     * 连续调用测试轮询效果
     */
    @GetMapping("/lb-test")
    public Map<String, Object> loadBalancerTest() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 连续调用10次，看是否会轮询
            for (int i = 0; i < 10; i++) {
                String response = restTemplate.getForObject(
                        "http://provider-service/user/health", String.class);
                result.put("call_" + i, response);
                log.info("调用 #{}: {}", i, response);
            }

        } catch (Exception e) {
            result.put("error", e.getMessage());
            log.error("调用出错", e);
        }

        return result;
    }
}