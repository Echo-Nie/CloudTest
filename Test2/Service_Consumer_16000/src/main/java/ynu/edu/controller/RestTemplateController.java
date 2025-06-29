package ynu.edu.controller;

import jakarta.annotation.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ynu.edu.entity.User;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class RestTemplateController {

    @Resource
    private RestTemplate restTemplate;

    private static final String SERVICE_URL = "http://provider-service/user";

    // GET方法：获取单个用户
    @GetMapping("/user/{id}")
    public User getUser(@PathVariable("id") Integer id) {
        return restTemplate.getForObject(SERVICE_URL + "/" + id, User.class);
    }

    // GET方法：获取所有用户
    @GetMapping("/user/list")
    public List<User> getAllUsers() {
        return restTemplate.getForObject(SERVICE_URL + "/list", List.class);
    }

    // POST方法：添加用户
    @PostMapping("/user/add")
    public String addUser(@RequestBody User user) {
        return restTemplate.postForObject(SERVICE_URL + "/add", user, String.class);
    }

    // PUT方法：更新用户
    @PutMapping("/user/update")
    public String updateUser(@RequestBody User user) {
        HttpEntity<User> requestEntity = new HttpEntity<>(user);
        ResponseEntity<String> response = restTemplate.exchange(
                SERVICE_URL + "/update",
                HttpMethod.PUT,
                requestEntity,
                String.class);
        return response.getBody();
    }

    // DELETE方法：删除用户
    @DeleteMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(null);
        ResponseEntity<String> response = restTemplate.exchange(
                SERVICE_URL + "/delete/" + id,
                HttpMethod.DELETE,
                requestEntity,
                String.class);
        return response.getBody();
    }
}