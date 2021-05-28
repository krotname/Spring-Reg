/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boots.bpd.graphql.exception;

/**
 *
 * @author ailori
 */
public class GraphQLParserException extends RuntimeException {

    public GraphQLParserException() {

    }

    public GraphQLParserException(String message) {
        super(message);
    }

    public GraphQLParserException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
