package ynu.edu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import ynu.edu.rule.CustomThreeTimeLoadBalanceConfig;

/**
 * 服务消费者应用程序
 * 配置了自定义的三次调用负载均衡策略和Resilience4j断路器
 */
@SpringBootApplication
@EnableFeignClients
@LoadBalancerClient(name = "provider-service", configuration = CustomThreeTimeLoadBalanceConfig.class)
public class ConsumerApplication16000 {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication16000.class, args);
    }

    /**
     * 配置带有负载均衡的RestTemplate
     * 默认使用轮询策略，通过@LoadBalancerClient注解会被覆盖为ThreeTimeLoadBalancer
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
