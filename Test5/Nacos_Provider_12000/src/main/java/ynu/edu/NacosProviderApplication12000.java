package ynu.edu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NacosProviderApplication12000 {
    public static void main(String[] args) {
        SpringApplication.run(NacosProviderApplication12000.class, args);
    }
} 