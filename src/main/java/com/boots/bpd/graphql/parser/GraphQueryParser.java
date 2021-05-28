/*
 * Copyright 2019 ailori.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.boots.bpd.graphql.parser;

import com.boots.bpd.graphql.annotations.GraphQLRequestOperation;
import com.boots.bpd.graphql.annotations.GraphQLRequestParameters;
import com.boots.bpd.graphql.annotations.GraphQLRequestResponse;
import com.boots.bpd.graphql.converter.GraphQLParser;
import com.boots.bpd.graphql.exception.GraphQLParserException;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Map;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

/**
 *
 * @author davies tobi alex
 */
public class GraphQueryParser implements GraphQLParser {

    private GraphQLWriter jsonWriter;

    private StringWriter writer;

    public GraphQueryParser(GraphQLWriter jsonWriter, StringWriter writer) {
        this.jsonWriter = jsonWriter;
        this.writer = writer;
    }

    public GraphQueryParser() {
        writer = new StringWriter();
        jsonWriter = new GraphQLWriter(writer);
    }

    /**
     * Converts a Class Object type to graphQL String
     *
     * @param <T>
     * @param object
     * @return
     * @throws Exception
     */
    @Override
    public <T> String toString(T object) throws GraphQLParserException, IOException, IllegalArgumentException, IllegalAccessException {

        for (Field f : object.getClass().getDeclaredFields()) {
            f.setAccessible(true);

            if (f.getAnnotation(GraphQLRequestOperation.class) != null) {
                toGraphQLString(f, OPERATION_PROCESSOR, object);
            } else if (f.getAnnotation(GraphQLRequestParameters.class) != null) {
                toGraphQLString(f, PARAMETER_PROCESSOR, object);
            } else if (f.getAnnotation(GraphQLRequestResponse.class) != null) {
                toGraphQLString(f, RESPONSE_BODY_PROCESSOR, object);
            } else {
                throw new GraphQLParserException("Class not annotated properly,can not parse object.");
            }
        }
        return writer.toString();
    }

    /**
     *
     * @param src
     * @param part
     * @param object
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void toGraphQLString(Field src, int part, Object object) throws IOException, IllegalArgumentException, IllegalAccessException {

        jsonWriter.beginObject();
        switch (part) {
            case OPERATION_PROCESSOR:
                jsonWriter.writeOperationName((String) src.get(object));
                break;
            case PARAMETER_PROCESSOR:
                jsonWriter.writeParameters((Map<String, Object>) src.get(object));
                break;
            case RESPONSE_BODY_PROCESSOR:
                jsonWriter.responseExected((Map<String, Object>) src.get(object));
                jsonWriter.endObject();
                break;
            default:
                throw new GraphQLParserException("Class not annotated properly,can not parse object.");
        }

    }

    /**
     * converts graphQl query string to class object
     *
     * @param <T>
     * @param query
     * @param classType
     * @return
     * @throws
     * org.springframework.boot.configurationprocessor.json.JSONException
     */
    @Override
    public <T> T parse(String query, String operationName, Class<T> classType) throws JSONException {
        JSONObject obj = new JSONObject(query);
        Gson g = new Gson();
        return g.fromJson(obj.getJSONObject("data").getString(operationName), classType);
//        throw new UnsupportedOperationException("This function has not been implemented yet");
    }
}
