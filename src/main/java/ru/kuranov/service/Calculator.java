package ru.kuranov.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.net.URISyntaxException;

import static ru.kuranov.value.DefaultValues.DEFAULT_FUNCTION_NAME;

@Service
public class Calculator {
    private static final Logger log = LoggerFactory.getLogger(Calculator.class);

    public String calc(String function, int x)
            throws ScriptException, NoSuchMethodException, IOException, URISyntaxException {

        // запускаем движок
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        engine.eval(function);
        Invocable inv = (Invocable) engine;

        // выполняем вычисления
        String result = String.valueOf(inv.invokeFunction(DEFAULT_FUNCTION_NAME.toString(), x));
        log.info("RESULT FUNCTION {}", result);
        return result;
    }
}
