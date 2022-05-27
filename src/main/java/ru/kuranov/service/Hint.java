package ru.kuranov.service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Hint {
    DEFAULT_FUNCTION("function func(a) {\n" +
            "  return a + 10;\n" +
            "}");

    private final String message;

    @Override
    public String toString() {
        return message;
    }
}
