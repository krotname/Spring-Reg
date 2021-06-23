package com.boots.parser;

import java.util.ArrayList;
import java.util.Map;

public interface Parser {
    public ArrayList<Map<String, String>> stringToMap(String s);
}
