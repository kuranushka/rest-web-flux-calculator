package ru.kuranov.service;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Validator {

    public boolean isValid(String data) {
        return isNumber(data) && Integer.parseInt(data) >= 1;
    }

    public boolean isValidPause(String data) {
        return isNumber(data);
    }

    private boolean isNumber(String data) {
        Pattern pattern = Pattern.compile("^\\d+$");
        Matcher matcher = pattern.matcher(String.valueOf(data));
        return matcher.find();
    }
}
