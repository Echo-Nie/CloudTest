package ynu.edu.controller;

import com.netflix.appinfo.InstanceInfo;

import jakarta.annotation.Resource;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ynu.edu.entity.Cart;
import ynu.edu.entity.User;
import ynu.edu.feign.ServiceProviderService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Resource
    private ServiceProviderService serviceInstance;

    @GetMapping("/getCartById/{userId}")
    public Cart getCartById(@PathVariable("userId") Integer userId){
        Cart cart = new Cart();
        List<String> goods=new ArrayList<>();
        goods.add("电池");
        goods.add("无人机");
        goods.add("笔记本电脑");
        cart.setGoodList(goods);
        User u = serviceInstance.GetUserById(userId);
        cart.setUser(u);
        return cart;
    }
}
