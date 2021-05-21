package com.boots.controller;

import com.boots.entity.User;
import com.boots.service.ProductService;
import com.boots.service.UserService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mountcloud.graphql.GraphqlClient;
import org.mountcloud.graphql.request.query.DefaultGraphqlQuery;
import org.mountcloud.graphql.request.query.GraphqlQuery;
import org.mountcloud.graphql.response.GraphqlResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;
    private static final Logger logger = LogManager.getLogger(ProductController.class);

    @GetMapping("/product")
    public String registration(Model model) {

        Object product = productService.getProduct();
        model.addAttribute("product", product);
        logger.info("product " + product);
        return "product";

    }

}
