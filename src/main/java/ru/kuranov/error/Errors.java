package ru.kuranov.error;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Errors {
    FUNCTION_ERRORS("КОД НЕ СООТВЕТСТВУЕТ ТРЕБОВАНИЯМ"),
    ONLY_DIGITS_MINIMUM_ONE("ИСПОЛЬЗУЙТЕ ДЛЯ ВВОДА ТОЛЬКО ЦИФРЫ, МИНИМАЛЬНОЕ КОЛИЧЕСТВО РАСЧЕТОВ 1");

    private final String message;

    @Override
    public String toString() {
        return message;
    }
}
