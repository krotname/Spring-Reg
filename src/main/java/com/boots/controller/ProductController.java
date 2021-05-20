package com.boots.controller;

import com.boots.entity.User;
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
    private UserService userService;
    private static final Logger logger = LogManager.getLogger(ProductController.class);

    @GetMapping("/product")
    public String registration(Model model) {


        GraphqlClient client = GraphqlClient.buildGraphqlClient("http://localhost:8080/v1/graphql");

        GraphqlQuery query = new DefaultGraphqlQuery("employee");
        query.addResultAttributes("lastname", "name");
        Map data = null;
        try {
            GraphqlResponse response = client.doQuery(query);
            data = response.getData();


        } catch (IOException e) {
            e.printStackTrace();
        }

        model.addAttribute("product", data);

        System.out.println(data);
        logger.info("product" + data);
        return "product";

    }

}
