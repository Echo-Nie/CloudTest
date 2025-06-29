package ynu.edu.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ynu.edu.entity.User;
import ynu.edu.feign.fallback.ServiceProviderFallback;

@FeignClient(value = "provider-service", fallback = ServiceProviderFallback.class)
public interface ServiceProviderService {
    @GetMapping("/user/{userId}")
    User GetUserById(@PathVariable("userId") Integer userId);
}
