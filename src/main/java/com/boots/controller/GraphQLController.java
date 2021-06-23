package com.boots.controller;

import com.boots.bpd.graphql.converter.GraphQLParser;
import com.boots.bpd.graphql.parser.GraphQueryParser;
import com.boots.entity.Employee;
import org.mountcloud.graphql.GraphqlClient;
import org.mountcloud.graphql.request.query.DefaultGraphqlQuery;
import org.mountcloud.graphql.request.query.GraphqlQuery;
import org.mountcloud.graphql.response.GraphqlResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GraphQLController {

    @Autowired
    private JmsTemplate jmsTemplate;

    GraphQLParser p = new GraphQueryParser();

    @GetMapping("/ActiveMQAdd/{m}")
    public String ActiveMQAdd(@PathVariable("m") String message) {
        jmsTemplate.convertAndSend("queue", message);
        return "Done";
    }

    @GetMapping("/GraphQLtest")
    public String GraphQLtest( Model model) {

        GraphqlClient client = GraphqlClient.buildGraphqlClient("http://localhost:8080/v1/graphql");
        Map<String, String> headers = new HashMap<String, String>();

        GraphqlQuery query = new DefaultGraphqlQuery("employee");
        query.addResultAttributes("lastname", "name");
        Map data = null;
        try {
            GraphqlResponse response = client.doQuery(query);
            data = response.getData();
            System.out.println(data);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Employee Employee = new Employee("0", "1");
        try {
            Employee = p.parse(data.toString(), "parse", Employee.class);

        } catch (Exception ex) {
        }

        System.out.println(Employee.getLastname());
        System.out.println(Employee.getName());
        return data.toString();

    }
}
