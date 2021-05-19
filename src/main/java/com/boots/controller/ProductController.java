package com.boots.controller;

import com.boots.entity.User;
import com.boots.service.UserService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductController {
    @Autowired
    private UserService userService;
    private static final Logger logger = LogManager.getLogger(ProductController.class);

    @GetMapping("/product")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());
        logger.info("product" + model.toString()) ;
        return "product";

    }

}
