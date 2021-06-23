package com.boots.controller;

import com.boots.bpd.graphql.converter.GraphQLParser;
import com.boots.bpd.graphql.parser.GraphQueryParser;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mountcloud.graphql.GraphqlClient;
import org.mountcloud.graphql.request.query.DefaultGraphqlQuery;
import org.mountcloud.graphql.request.query.GraphqlQuery;
import org.mountcloud.graphql.response.GraphqlResponse;
import org.springframework.boot.configurationprocessor.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class GraphQLtest {
    static GraphQLParser p = new GraphQueryParser();

    public static void main(String[] args) throws IOException, IllegalAccessException, JSONException {
        GraphqlClient client = GraphqlClient.buildGraphqlClient("http://localhost:8080/v1/graphql");
        GraphqlQuery query = new DefaultGraphqlQuery("employee (limit: 2)");
        query.addResultAttributes("lastname", "name");
        GraphqlResponse response = client.doQuery(query);
        Map data = response.getData();
        System.out.println(data.toString()); // {data={employee=[{lastname=Ivanov, name=Ivan}]}}
        Response response1 = convertMapToObject(data);
        System.out.println(response1.data.employee.get(1).name);  //Ivan
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
        @JsonProperty("employee")
        private List<Employee> employee;
    }

    public static class Employee {
        @JsonProperty("name")
        private String name;

        @JsonProperty("lastname")
        private String lastname;
    }

}
