package ynu.edu.feign.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ynu.edu.entity.User;
import ynu.edu.feign.UserFeignClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserFeignFallback implements UserFeignClient {

    private static final Logger log = LoggerFactory.getLogger(UserFeignFallback.class);

    @Override
    public User getUserById(Integer id) {
        log.warn("用户服务降级: getUserById({})", id);
        User fallbackUser = new User();
        fallbackUser.setUserId(id != null ? id : 0);
        fallbackUser.setUserName("降级服务-用户不存在");
        fallbackUser.setPassWord("");
        fallbackUser.setAddress("降级服务");
        return fallbackUser;
    }

    @Override
    public List<User> getAllUsers() {
        log.warn("用户服务降级: getAllUsers()");
        List<User> fallbackUsers = new ArrayList<>();
        User fallbackUser = new User();
        fallbackUser.setUserId(0);
        fallbackUser.setUserName("降级服务-查询失败");
        fallbackUser.setPassWord("");
        fallbackUser.setAddress("降级服务");
        fallbackUsers.add(fallbackUser);
        return fallbackUsers;
    }
}