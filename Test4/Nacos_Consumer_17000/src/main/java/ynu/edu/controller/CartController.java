package ynu.edu.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ynu.edu.entity.Cart;
import ynu.edu.entity.User;
import ynu.edu.feign.NacosProviderService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Resource
    private NacosProviderService nacosProviderService;

    @GetMapping("/getCartById/{userId}")
    public Cart getCartById(@PathVariable("userId") Integer userId){
        Cart cart = new Cart();
        List<String> goods=new ArrayList<>();
        goods.add("电池");
        goods.add("无人机");
        goods.add("笔记本电脑");
        cart.setGoodList(goods);
        User u = nacosProviderService.GetUserById(userId);
        cart.setUser(u);
        return cart;
    }
} 