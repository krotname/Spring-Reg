package com.boots.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GraphQLController {


    @GetMapping("/GraphQLtest")
    public String GraphQLtest(Model model) {

        return "GraphQLtest";

    }
}
