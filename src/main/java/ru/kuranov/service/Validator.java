package ru.kuranov.service;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Validator {

    public boolean isValidate(String data) {

        // валидируем числовые данные
        Pattern pattern = Pattern.compile("^\\d+$");
        Matcher matcher = pattern.matcher(String.valueOf(data));
        return matcher.find() && Integer.parseInt(data) >= 1;
    }
}
