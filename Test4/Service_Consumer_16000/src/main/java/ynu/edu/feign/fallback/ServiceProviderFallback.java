package ynu.edu.feign.fallback;

import org.springframework.stereotype.Component;
import ynu.edu.entity.User;
import ynu.edu.feign.ServiceProviderService;

@Component
public class ServiceProviderFallback implements ServiceProviderService {

    @Override
    public User GetUserById(Integer userId) {
        User fallbackUser = new User();
        fallbackUser.setUserId(0);
        fallbackUser.setUserName("降级服务-获取用户失败");
        fallbackUser.setPassWord("");
        fallbackUser.setAddress("降级服务");
        return fallbackUser;
    }
}