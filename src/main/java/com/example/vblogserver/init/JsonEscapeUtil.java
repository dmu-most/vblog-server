package com.example.vblogserver.init;

import org.springframework.stereotype.Service;

@Service
public class JsonEscapeUtil {
    public String escapeDoubleQuotes(String input) {
        return input.replace("\"", "\\\"");
    }
}
