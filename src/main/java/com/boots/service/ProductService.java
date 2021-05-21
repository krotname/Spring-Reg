package com.boots.service;

import org.mountcloud.graphql.GraphqlClient;
import org.mountcloud.graphql.request.query.DefaultGraphqlQuery;
import org.mountcloud.graphql.request.query.GraphqlQuery;
import org.mountcloud.graphql.response.GraphqlResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class ProductService {

    public Object getProduct(){
        GraphqlClient client = GraphqlClient.buildGraphqlClient("http://localhost:8080/v1/graphql");

        GraphqlQuery query = new DefaultGraphqlQuery("product");
        query.addResultAttributes("name", "color", "created_at", "description", "secret");
        Map data = null;
        try {
            GraphqlResponse response = client.doQuery(query);
            data = response.getData();

            for (Object entry: data.entrySet())
                System.out.println(entry);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;

    }
}
