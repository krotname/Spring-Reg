package com.boots.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"data\": {\"product\": [{\"name\": \"Ангара-А5\", \"color\": \"Белый\", \"created_at\": \"2020-02-02T00:00:00+00:00\", \"secret\": false }, {\"name\": \"Falcon Heavy\", \"color\": \"Белый\", \"created_at\": \"2021-02-02T00:00:00+00:00\", \"secret\": false }] } }";
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference() {};
        Map<Object, Object> map = mapper.readValue(json, typeRef);
        Response response = convertMapToObject(map);
        System.out.println(response.data.product.get(0).name);
    }

    private static Response convertMapToObject(Map<Object, Object> map) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"));
        return mapper.convertValue(map, Response.class);
    }

    public static class Response {
        @JsonProperty("data")
        private Data data;
    }

    public static class Data {
        @JsonProperty("product")
        private List<Product> product;
    }

    public static class Product {
        @JsonProperty("name")
        private String name;

        @JsonProperty("color")
        private String color;

        @JsonProperty("created_at")
        private Date createdAt;

        @JsonProperty("secret")
        private boolean secret;
    }
}
 