/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boots.bpd.graphql.converter;

import com.boots.bpd.graphql.exception.GraphQLParserException;
import java.io.IOException;
import java.lang.reflect.Field;
import org.springframework.boot.configurationprocessor.json.JSONException;

/**
 *
 * @author ailori
 */
public interface GraphQLParser {

    public final int OPERATION_PROCESSOR = 0;

    public final int PARAMETER_PROCESSOR = 1;

    public final int RESPONSE_BODY_PROCESSOR = 2;

    /**
     * Converts a Class Object type to graphQL String
     *
     * @param <T>
     * @param object
     * @return
     * @throws GraphQLParserException
     */
    public <T> String toString(T object) throws GraphQLParserException, IOException, IllegalArgumentException, IllegalAccessException;

    /**
     * Convert string to GraphQL Object
     *
     * @param <T>
     * @param query
     * @param operationName
     * @return
     * @throws
     * org.springframework.boot.configurationprocessor.json.JSONException
     */
    public <T> T parse(String query, String operationName, Class<T> classType) throws JSONException;
}
