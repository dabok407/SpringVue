package com.mk.vue.controller.page;

import com.mk.vue.service.AdminMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/pages")
public class PageController {

    @Autowired
    private AdminMenuService adminMenuService;

    @RequestMapping("/login")
    public String login() {
        return "pages/login/login";
    }

    @RequestMapping("/loginSuccess")
    public String loginSuccess() {
        return "pages/login/loginSuccess";
    }

    @RequestMapping("/logout")
    public String logout() {
        return "pages/login/logout";
    }

    @RequestMapping(path = {""})
    public ModelAndView index() {
        return new ModelAndView("/pages/main")
                .addObject("menuList", adminMenuService.getAdminMenu())
                .addObject("code", "main")
                ;
    }

    @RequestMapping("/user")
    public ModelAndView user() {
        return new ModelAndView("/pages/user/user")
                .addObject("menuList", adminMenuService.getAdminMenu())
                .addObject("code", "user")
                ;
    }

    @RequestMapping("/partner")
    public ModelAndView partner() {
        return new ModelAndView("/pages/partner/partner")
                .addObject("menuList", adminMenuService.getAdminMenu())
                .addObject("code", "partner")
                ;
    }

    @RequestMapping("/item")
    public ModelAndView item() {
        return new ModelAndView("/pages/item/item")
                .addObject("menuList", adminMenuService.getAdminMenu())
                .addObject("code", "item")
                ;
    }

    @RequestMapping("/itemRegistPopup")
    public ModelAndView itemRegistPopup() {
        return new ModelAndView("/pages/item/itemRegistPopup")
                .addObject("menuList", adminMenuService.getAdminMenu())
                .addObject("code", "itemRegistPopup")
                ;
    }

    @RequestMapping("/adminUser")
    public ModelAndView adminUser() {
        return new ModelAndView("/pages/admin/adminUser")
                .addObject("menuList", adminMenuService.getAdminMenu())
                .addObject("code", "adminUser")
                ;
    }

    @RequestMapping("/orderGroup")
    public ModelAndView order() {
        return new ModelAndView("/pages/order/orderGroup")
                .addObject("menuList", adminMenuService.getAdminMenu())
                .addObject("code", "orderGroup")
                ;
    }

    @RequestMapping("/vue")
    public ModelAndView vue() {
        ModelAndView modelAndView = new ModelAndView("/pages/index");
        /*modelAndView.addObject("menuList", adminMenuService.getAdminMenu());
        modelAndView.addObject("code", "orderGroup");*/
        return modelAndView;
    }

}
