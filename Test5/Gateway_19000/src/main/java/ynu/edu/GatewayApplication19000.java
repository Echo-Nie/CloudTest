package ynu.edu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Gateway网关服务主启动类
 */
@SpringBootApplication
@EnableDiscoveryClient // 开启服务注册与发现
public class GatewayApplication19000 {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication19000.class, args);
    }
} 