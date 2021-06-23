package com.boots.service;

import com.boots.entity.Product;
import com.boots.parser.Parser;
import com.boots.parser.ParserImplementation;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.mountcloud.graphql.GraphqlClient;
import org.mountcloud.graphql.request.query.DefaultGraphqlQuery;
import org.mountcloud.graphql.request.query.GraphqlQuery;
import org.mountcloud.graphql.response.GraphqlResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
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

        Parser graphQLParser = new ParserImplementation();
        ArrayList<Map<String, String>> maps = graphQLParser.stringToMap(data.toString());

        Gson gson = new Gson();
        ArrayList<Product> products = new ArrayList<>();

        for (Map<String, String> m:maps
        ) {
            JsonElement jsonElement = gson.toJsonTree(m);
            Product product = gson.fromJson(jsonElement, Product.class);
            products.add(product);
        }

        return products;

    }
}
