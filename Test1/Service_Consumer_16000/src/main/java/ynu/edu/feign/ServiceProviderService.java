package ynu.edu.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ynu.edu.entity.User;

@FeignClient("provider-service")
public interface ServiceProviderService {
    @GetMapping("/user/getUserById/{userId}")
    User GetUserById(@PathVariable("userId") Integer userId);
}
