package com.boots.service;

import com.boots.entity.Product;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.nfl.glitr.Glitr;
import com.nfl.glitr.GlitrBuilder;
import graphql.GraphQL;
import org.mountcloud.graphql.GraphqlClient;
import org.mountcloud.graphql.request.GraphqlRequest;
import org.mountcloud.graphql.request.query.DefaultGraphqlQuery;
import org.mountcloud.graphql.request.query.GraphqlQuery;
import org.mountcloud.graphql.response.GraphqlResponse;
import org.mountcloud.graphql.response.GraphqlState;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProductTest {
    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();

        GraphqlClient client = GraphqlClient.buildGraphqlClient("http://localhost:8080/v1/graphql");

        GraphqlQuery query = new DefaultGraphqlQuery("product");
        query.addResultAttributes("name", "color", "created_at", "secret");
        Map data = null;
        try {
            GraphqlResponse response = client.doQuery(query);
            data = response.getData();

            for (Object entry : data.entrySet())
                System.out.println(entry);


        } catch (IOException e) {
            e.printStackTrace();
        }


        Glitr glitr = GlitrBuilder.newGlitr()
                .withQueryRoot(new Product())
                .build();

        GraphQL graphQL = new GraphQL(glitr.getSchema());

        Map<String, Object> result = graphQL.execute("{hello}").getData();

        System.out.println(result); // Prints: {hello=World!}

        Map<String, Object> resul2 = graphQL.execute("{data={product=[{name=Ангара-А5, color=Белый, created_at=2020-02-02T00:00:00+00:00, secret=false}, {name=Falcon Heavy, color=Белый, created_at=2021-02-02T00:00:00+00:00, secret=false}]}}").getData();
        System.out.println(resul2);


        Product product = new Product("А55", "синий", "05.05.05", "25.05.05", "false");
        String s = gson.toJson(product);
        System.out.println(s);
        System.out.println(" ----- " + data.toString());
        String datas = data.toString().substring(16, data.toString().length() - 3);
        System.out.println(datas);
        Product product1 = gson.fromJson(datas, Product.class);
        System.out.println(product1);
    }
}
