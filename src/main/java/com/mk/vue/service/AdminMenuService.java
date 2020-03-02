package com.mk.vue.service;

import com.mk.vue.model.front.AdminMenu;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AdminMenuService {

    public List<AdminMenu> getAdminMenu(){

        return Arrays.asList(
                AdminMenu.builder().title("고객").url("/pages/user/").code("user").build()
                ,AdminMenu.builder().title("파트너").url("/pages/partner/").code("partner").build()
                ,AdminMenu.builder().title("상품").url("/pages/item").code("item").build()
                ,AdminMenu.builder().title("주문").url("/pages/orderGroup").code("orderGroup").build()
                /*,AdminMenu.builder().title("주문 관리").url("/pages/order").code("order").build()*/
                ,AdminMenu.builder().title("사용자").url("/pages/adminUser").code("adminUser").build()
        );

    }

}
