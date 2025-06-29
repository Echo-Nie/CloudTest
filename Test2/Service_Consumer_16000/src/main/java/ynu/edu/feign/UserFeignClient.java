package ynu.edu.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ynu.edu.entity.User;
import ynu.edu.rule.CustomThreeTimeLoadBalanceConfig;

import java.util.List;

@FeignClient(name = "provider-service", configuration = CustomThreeTimeLoadBalanceConfig.class)
public interface UserFeignClient {

    @GetMapping("/user/{id}")
    User getUserById(@PathVariable("id") Integer id);

    @GetMapping("/user/list")
    List<User> getAllUsers();

    @PostMapping("/user/add")
    String addUser(@RequestBody User user);

    @PutMapping("/user/update")
    String updateUser(@RequestBody User user);

    @DeleteMapping("/user/delete/{id}")
    String deleteUser(@PathVariable("id") Integer id);

    @GetMapping("/user/health")
    String checkHealth();
}