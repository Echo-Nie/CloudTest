package ynu.edu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import ynu.edu.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RefreshScope // 配置自动刷新注解
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private static final String SERVER_ID = "11000";

    @Value("${config.info:default value}")
    private String configInfo;

    // 模拟数据库
    private static Map<Integer, User> userMap = new HashMap<>();

    static {
        userMap.put(1, new User(1, "张三", "123456", "北京市"));
        userMap.put(2, new User(2, "李四", "654321", "上海市"));
        userMap.put(3, new User(3, "王五", "111111", "广州市"));
    }

    // GET方法：查询用户
    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Integer id) {
        log.info("【服务{}】接收到请求：获取用户ID={}", SERVER_ID, id);
        User user = userMap.get(id);
        if (user != null) {
            // 创建新的用户对象，避免修改原始数据
            User responseUser = new User(
                    user.getUserId(),
                    user.getUserName(),
                    user.getPassWord(),
                    user.getAddress());
            responseUser.setAddress(user.getAddress() + " (from " + SERVER_ID + ")");
            return responseUser;
        }
        return null;
    }

    // GET方法：查询所有用户
    @GetMapping("/list")
    public List<User> getAllUsers() {
        log.info("【服务{}】接收到请求：获取所有用户", SERVER_ID);
        List<User> users = new ArrayList<>();
        for (User user : userMap.values()) {
            // 创建新的用户对象，避免修改原始数据
            User responseUser = new User(
                    user.getUserId(),
                    user.getUserName(),
                    user.getPassWord(),
                    user.getAddress() + " (from " + SERVER_ID + ")");
            users.add(responseUser);
        }
        return users;
    }

    // POST方法：创建用户
    @PostMapping("/add")
    public String addUser(@RequestBody User user) {
        log.info("【服务{}】接收到请求：添加用户 {}", SERVER_ID, user.getUserName());
        userMap.put(user.getUserId(), user);
        return "添加用户成功：" + user.getUserName() + " (from " + SERVER_ID + ")";
    }

    // PUT方法：更新用户
    @PutMapping("/update")
    public String updateUser(@RequestBody User user) {
        log.info("【服务{}】接收到请求：更新用户ID={}", SERVER_ID, user.getUserId());
        if (userMap.containsKey(user.getUserId())) {
            userMap.put(user.getUserId(), user);
            return "更新用户成功：" + user.getUserName() + " (from " + SERVER_ID + ")";
        } else {
            return "用户不存在，无法更新 (from " + SERVER_ID + ")";
        }
    }

    // DELETE方法：删除用户
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id) {
        log.info("【服务{}】接收到请求：删除用户ID={}", SERVER_ID, id);
        if (userMap.containsKey(id)) {
            String name = userMap.get(id).getUserName();
            userMap.remove(id);
            return "删除用户成功：" + name + " (from " + SERVER_ID + ")";
        } else {
            return "用户不存在，无法删除 (from " + SERVER_ID + ")";
        }
    }

    // 添加一个健康检查接口，用于验证负载均衡
    @GetMapping("/health")
    public String health() {
        log.info("【服务{}】接收到健康检查请求", SERVER_ID);
        return "Service " + SERVER_ID + " is healthy!";
    }

    // 测试配置中心的接口
    @GetMapping("/config")
    public String getConfig() {
        return "Config Info: " + configInfo + " (from " + SERVER_ID + ")";
    }
}
