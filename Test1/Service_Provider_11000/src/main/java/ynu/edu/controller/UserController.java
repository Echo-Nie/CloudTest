package ynu.edu.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ynu.edu.entity.User;

@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping("/getUserById/{userId}")
    public User GetUserById(@PathVariable("userId") Integer userId){
        User user = new User();
        user.setUserId(userId);
        user.setUserName("小明 from 11000");
        user.setPassWord("123456");
        return user;
    }
}
