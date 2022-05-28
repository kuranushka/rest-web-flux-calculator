package ru.kuranov.error;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Errors {

    FUNCTION_ERRORS("КОД НЕ СООТВЕТСТВУЕТ ТРЕБОВАНИЯМ"),
    USE_ONLY_DIGITS_MINIMUM_ITERATION_ONE("ИСПОЛЬЗУЙТЕ ДЛЯ ВВОДА ТОЛЬКО ЦИФРЫ, МИНИМАЛЬНОЕ КОЛИЧЕСТВО РАСЧЕТОВ 1"),
    USE_ONLY_DIGITS_AND_MINIMUM_PERIOD_ONE("ИСПОЛЬЗУЙТЕ ДЛЯ ВВОДА ТОЛЬКО ЦИФРЫ, МИНИМАЛЬНЫЙ ПЕРИОД 1 МИЛЛИСЕКУНДА"),
    USE_ONLY_DIGITS("ИСПОЛЬЗУЙТЕ ДЛЯ ВВОДА ТОЛЬКО ЦИФРЫ");

    private final String message;

    @Override
    public String toString() {
        return message;
    }
}
