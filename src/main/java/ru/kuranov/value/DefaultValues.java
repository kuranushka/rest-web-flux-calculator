package ru.kuranov.value;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DefaultValues {

    DEFAULT_FUNCTION("function func(a) {\n" +
            "  return a + 10;\n" +
            "}"),
    DEFAULT_PERIOD("100"),
    DEFAULT_ITERATION("25"),
    DEFAULT_PAUSE("0"),
    DEFAULT_FUNCTION_NAME("func");

    private final String message;

    @Override
    public String toString() {
        return message;
    }
}
