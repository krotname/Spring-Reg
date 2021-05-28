/*
 * Copyright 2019 davies tobi alex.
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

import com.boots.bpd.graphql.config.GraphQLScope;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author davies tobi alex
 */
public class GraphQLWriter extends GraphQLScope implements Closeable, Flushable {

    /*
   * From RFC 7159, "All Unicode characters may be placed within the
   * quotation marks except for the characters that must be escaped:
   * quotation mark, reverse solidus, and the control characters
   * (U+0000 through U+001F)."
   *
   * We also escape '\u2028' and '\u2029', which JavaScript interprets as
   * newline characters. This prevents eval() from failing with a syntax
   * error. http://code.google.com/p/google-gson/issues/detail?id=341
     */
    private static final String[] REPLACEMENT_CHARS;
    private static final String[] HTML_SAFE_REPLACEMENT_CHARS;

    private int[] stack = new int[32];
    private int stackSize = 0;

    static {
        REPLACEMENT_CHARS = new String[128];
        for (int i = 0; i <= 0x1f; i++) {
            REPLACEMENT_CHARS[i] = String.format("\\u%04x", (int) i);
        }
        REPLACEMENT_CHARS['"'] = "\\\"";
        REPLACEMENT_CHARS['\\'] = "\\\\";
        REPLACEMENT_CHARS['\t'] = "\\t";
        REPLACEMENT_CHARS['\b'] = "\\b";
        REPLACEMENT_CHARS['\n'] = "\\n";
        REPLACEMENT_CHARS['\r'] = "\\r";
        REPLACEMENT_CHARS['\f'] = "\\f";
        HTML_SAFE_REPLACEMENT_CHARS = REPLACEMENT_CHARS.clone();
        HTML_SAFE_REPLACEMENT_CHARS['<'] = "\\u003c";
        HTML_SAFE_REPLACEMENT_CHARS['>'] = "\\u003e";
        HTML_SAFE_REPLACEMENT_CHARS['&'] = "\\u0026";
        HTML_SAFE_REPLACEMENT_CHARS['='] = "\\u003d";
        HTML_SAFE_REPLACEMENT_CHARS['\''] = "\\u0027";
    }

    {
        push(EMPTY_DOCUMENT);
    }

    /**
     * set or not to set single space after certain strokes
     */
    private boolean indentation = false;

    /**
     * beautifies the GraphQL payload
     */
    private boolean prettyfy = false;

    /**
     * The output data, containing at most one top-level array or object.
     */
    private final Writer out;

    /**
     * Creates a new instance that writes a JSON-encoded stream to {@code out}.
     * For best performance, ensure {@link Writer} is buffered; wrapping in
     * {@link java.io.BufferedWriter BufferedWriter} if necessary.
     */
    public GraphQLWriter(Writer out) {
        if (out == null) {
            throw new NullPointerException("out == null");
        }
        this.out = out;
    }

    /**
     * Begin with default stack size and begin to grow the stack as new strokes
     * are added
     *
     * @param newTop
     */
    private void push(int newTop) {

        //If and when stack size grows to max predefined length, double the
        //length and continue processing
        if (stackSize == stack.length) {
            int[] newStack = new int[stackSize * 2];
            System.arraycopy(stack, 0, newStack, 0, stackSize);
            stack = newStack;
        }
        stack[stackSize++] = newTop;
    }

    /**
     * Replace the value on the top of the stack with the given value.
     */
    private void replaceTop(int topOfStack) {
        stack[stackSize - 1] = topOfStack;
    }

    private int peek() {
        if (stackSize == 0) {
            throw new IllegalStateException("Error Parser has been reset, it means parse porcess has ended aburtly");
        }
        return stack[stackSize - 1];
    }

    /**
     * Inserts any necessary separators and whitespace before a literal value,
     * inline array, or inline object. Also adjusts the stack to expect either a
     * closing bracket or another element.
     */
    @SuppressWarnings("fallthrough")
    private void beforeAddStroke() throws IOException {
        switch (peek()) {
            case EMPTY_DOCUMENT: // first in document, meaning document i no longer empty
                replaceTop(NONEMPTY_DOCUMENT);
                break;
            case EMPTY_OBJECT:
                replaceTop(NONEMPTY_DOCUMENT);
                break;
            default:
                throw new IllegalStateException("Nesting problem.");
        }
    }

    public void writeOperationName(String operationName) throws IOException {
        push(NONEMPTY_OBJECT);
        this.write(operationName);
    }

    public void writeParameters(Map<String, Object> parameters) throws IOException {
        int size = 1;
        if (parameters != null && !parameters.isEmpty()) {
            beginParameter();
            Iterator<Map.Entry<String, Object>> itr = parameters.entrySet().iterator();
            while (itr.hasNext() && size < parameters.size()) {
                writeSingleParameter(itr.next(), ",");
                size++;
            }
            writeSingleParameter(itr.next(), this.indentation ? " " : "");
            endParameter();
        } else {
            push(NONPARAMETERS);
        }
    }

    /**
     * Writes a single parameter to the parameters section
     *
     * @param V
     */
    private void writeSingleParameter(Map.Entry<String, Object> V, String seperator) throws IOException {
        if (V.getValue() instanceof Integer) {
            this.write(V.getKey().concat(":").concat(Integer.toString((int) V.getValue())).concat(seperator));
        } else if (V.getValue() instanceof Double) {
            this.write(V.getKey().concat(":").concat(Double.toString((double) V.getValue())).concat(seperator));
        } else if (V.getValue() instanceof Float) {
            this.write(V.getKey().concat(":").concat(Float.toString((float) V.getValue())).concat(seperator));
        } else if (V.getValue() instanceof String) {
            this.write(V.getKey().concat(":").concat((String) V.getValue()).concat(seperator));
        } else if (V.getValue() instanceof Object) {
            // write new object
        } else if (V.getValue() == null) {
            throw new IllegalStateException("Invalid paramter value, paramter value can not be null");
        }
    }

    /**
     * All the responses expected
     *
     * @param map
     */
    public void responseExected(Map<String, Object> responses) throws IOException {
        int size = 1;
        if (responses != null && !responses.isEmpty()) {
            Iterator<Map.Entry<String, Object>> itr = responses.entrySet().iterator();
            while (itr.hasNext() && size < responses.size()) {
                writeSingleResponse(itr.next(), " ");
                size++;
            }
            writeSingleResponse(itr.next(), "");
            endObject();
        } else {
            throw new IllegalStateException("Invalid Parse Object, expecting expected responses");
        }
    }

    private void writeSingleResponse(Map.Entry<String, Object> V, String seperator) throws IOException {
        if (V.getKey() != null) {
            this.write(V.getKey().concat(seperator));
            if (V.getValue() != null && V.getValue() instanceof List<?>) {
                // write reposne object
                writeResposeObjects((String[]) ((List<String>) V.getValue()).toArray());
            } else if (V.getValue() != null && V.getValue() instanceof String[]) {
                writeResposeObjects((String[]) V.getValue());
            }
        } else {
            throw new IllegalStateException("Invalid Parse Object");
        }
    }

    private void writeResposeObjects(String[] object) throws IOException {
        this.beginObject();
        int less = object.length - 1;
        for (int i = 0; i < less; i++) {
            this.write(((String) object[i]).concat(" "));
        }
        this.write(((String) object[less]).concat(""));
        this.endObject();

    }

    public void beginObject() throws IOException {
        if (this.peek() == EMPTY_DOCUMENT || this.peek() == EMPTY_OBJECT) {
            this.beforeAddStroke();
            this.push(EMPTY_OBJECT);
            // this is the very genesis of the json
            if (this.indentation) {
                this.write("{ ");
            } else {
                this.write("{");
            }
//            newline();
        } else {
            // something else occuring yet to figure this out
        }
    }

    public void endObject() throws IOException {
        push(EMPTY_OBJECT);
        if (this.indentation) {
            this.write(" }");
        } else {
            this.write("} ");
        }
    }

    public void beginParameter() throws IOException {
        push(PARAMETERS);
        if (this.indentation) {
            this.write("( ");
        } else {
            this.write("(");
        }
    }

    public void endParameter() throws IOException {
        push(EMPTY_OBJECT);
        if (this.indentation) {
            this.write(" )");
        } else {
            this.write(")");
        }
    }

    private void newline() throws IOException {
        out.write("\n");
    }

    private void write(String stroke) throws IOException {
        out.write(stroke);
    }

    private void write(char stroke) throws IOException {
        out.write(stroke);
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    /**
     * Indentates the qraphql String
     *
     * @param indentation
     */
    public void setIndentation(boolean indentation) {
        this.indentation = indentation;
    }

    /**
     * Prettifys' the qraphQL string
     *
     * @param prettyfy
     */
    public void setPrettyfy(boolean prettyfy) {
        this.prettyfy = prettyfy;
    }

}
