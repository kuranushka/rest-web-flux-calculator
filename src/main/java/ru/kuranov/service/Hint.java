package ru.kuranov.service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Hint {
    DEFAULT_FUNCTION("function func(a) {\n" +
            "  return a + 10;\n" +
            "}"),
    DEFAULT_PERIOD("100"),
    DEFAULT_ITERATION("25");

    private final String message;

    @Override
    public String toString() {
        return message;
    }
}
