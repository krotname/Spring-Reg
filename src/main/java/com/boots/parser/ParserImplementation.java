package com.boots.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParserImplementation implements Parser {

    @Override

    public ArrayList<Map<String, String>> stringToMap(String s) {
        if (!s.startsWith("{")) throw new IllegalArgumentException();
        if (!s.contains("[")) throw new IllegalArgumentException();
        if (!s.contains("}")) throw new IllegalArgumentException();
        if (!s.contains("=")) throw new IllegalArgumentException();

        System.out.println(s);
        ArrayList<Map<String, String>> result = new ArrayList<>();
        String[] split1 = s.split("\\[");
        String[] split2 = split1[1].split(", ");

        Map<String, String> stringStringMap = new HashMap<>();

        for (String t : split2
        ) {
            if (t.startsWith("{")) {
                stringStringMap = new HashMap<>();
                t = t.substring(1);
                String[] split = t.split("=");
                stringStringMap.put(split[0], split[1]);
            } else if (t.endsWith("}]}}")) {
                t = t.substring(0, t.length() - 4);
                String[] split = t.split("=");
                stringStringMap.put(split[0], split[1]);
                result.add(stringStringMap);
                break;
            } else if (t.endsWith("}")) {
                t = t.substring(0, t.length() - 1);
                String[] split = t.split("=");
                stringStringMap.put(split[0], split[1]);
                result.add(stringStringMap);
            } else {
                String[] split = t.split("=");
                stringStringMap.put(split[0], split[1]);
            }
        }

        return result;
    }
}
