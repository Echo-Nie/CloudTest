package ynu.edu.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ynu.edu.entity.User;
import ynu.edu.feign.fallback.UserFeignFallback;
import ynu.edu.rule.CustomThreeTimeLoadBalanceConfig;

import java.util.List;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "provider-service", configuration = CustomThreeTimeLoadBalanceConfig.class, fallback = UserFeignFallback.class)
public interface UserFeignClient {

    /**
     * 根据用户ID获取用户信息
     * 
     * @param id 用户ID
     * @return 用户对象
     */
    @GetMapping("/user/{id}")
    User getUserById(@PathVariable("id") Integer id);

    /**
     * 获取所有用户信息
     * 
     * @return 用户列表
     */
    @GetMapping("/user/list")
    List<User> getAllUsers();
}