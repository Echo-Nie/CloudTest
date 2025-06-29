package ynu.edu.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import ynu.edu.entity.User;
import ynu.edu.feign.UserFeignClient;

import java.util.List;

@RestController
@RequestMapping("/feign")
public class FeignController {

    @Resource
    private UserFeignClient userFeignClient;

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable("id") Integer id) {
        return userFeignClient.getUserById(id);
    }

    @GetMapping("/user/list")
    public List<User> getAllUsers() {
        return userFeignClient.getAllUsers();
    }

    @PostMapping("/user/add")
    public String addUser(@RequestBody User user) {
        return userFeignClient.addUser(user);
    }

    @PutMapping("/user/update")
    public String updateUser(@RequestBody User user) {
        return userFeignClient.updateUser(user);
    }

    @DeleteMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id) {
        return userFeignClient.deleteUser(id);
    }
}