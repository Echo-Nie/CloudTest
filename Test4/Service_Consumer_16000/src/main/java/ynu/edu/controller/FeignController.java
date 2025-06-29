package ynu.edu.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import ynu.edu.entity.User;
import ynu.edu.feign.UserFeignClient;

import java.util.List;

/**
 * Feign客户端演示控制器
 * 使用OpenFeign调用远程服务
 */
@RestController
@RequestMapping("/feign")
public class FeignController {

    @Resource
    private UserFeignClient userFeignClient;

    /**
     * 根据ID获取用户信息
     * 
     * @param id 用户ID
     * @return 用户对象
     */
    @GetMapping("/user/{id}")
    public User getUser(@PathVariable("id") Integer id) {
        return userFeignClient.getUserById(id);
    }

    /**
     * 获取所有用户信息
     * 
     * @return 用户列表
     */
    @GetMapping("/user/list")
    public List<User> getAllUsers() {
        return userFeignClient.getAllUsers();
    }

    /**
     * 模拟添加用户（已简化，不再调用远程服务）
     * 
     * @param user 用户对象
     * @return 操作结果
     */
    @PostMapping("/user/add")
    public String addUser(@RequestBody User user) {
        return "添加用户功能已简化，不再调用远程服务: " + user.getUserName();
    }

    /**
     * 模拟更新用户（已简化，不再调用远程服务）
     * 
     * @param user 用户对象
     * @return 操作结果
     */
    @PutMapping("/user/update")
    public String updateUser(@RequestBody User user) {
        return "更新用户功能已简化，不再调用远程服务: " + user.getUserName();
    }

    /**
     * 模拟删除用户（已简化，不再调用远程服务）
     * 
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id) {
        return "删除用户功能已简化，不再调用远程服务: " + id;
    }
}