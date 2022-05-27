package ru.kuranov.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kuranov.dto.RequestDto;
import ru.kuranov.service.Calculator;
import ru.kuranov.service.Validator;

import javax.script.ScriptException;
import java.io.IOException;
import java.net.URISyntaxException;

import static ru.kuranov.error.Errors.*;
import static ru.kuranov.service.Hint.DEFAULT_FUNCTION;

@Controller
@RequiredArgsConstructor
public class RequestController {

    private static final Logger log = LoggerFactory.getLogger(RequestController.class);
    private final Calculator calculator;
    private final Validator validator;

    @GetMapping
    public String handleGet(Model model) {

        // передаём DTO на view для трансфера данных
        RequestDto requestDto = RequestDto.builder()
                .functionA(DEFAULT_FUNCTION.toString())
                .functionB(DEFAULT_FUNCTION.toString())
                .period("100")
                .build();

        model.addAttribute("requestDto", requestDto);
        return "page";
    }

    @PostMapping
    public String handlePost(@RequestParam(required = false) boolean isOrderedOut, RequestDto requestDto, Model model,
                             RedirectAttributes redirectAttributes) throws ScriptException, NoSuchMethodException, IOException, URISyntaxException {

        // проверяем поступившие скрипты функций
        calculator.calc(requestDto.getFunctionA(), 1);
        calculator.calc(requestDto.getFunctionB(), 1);

        // валидируем количество итераций
        if (!validator.isValidate(requestDto.getIteration())) {
            model.addAttribute("errorIteration", USE_ONLY_DIGITS_MINIMUM_ITERATION_ONE);
            return "page";
        }

        // валидируем период итераций
        if (!validator.isValidate(requestDto.getPeriod())) {
            model.addAttribute("errorPeriod", USE_ONLY_DIGITS_AND_MINIMUM_PERIOD_ONE);
            return "page";
        }

        // перенаправляем данные в WebFlux REST
        redirectAttributes.addAttribute("funcA", requestDto.getFunctionA());
        redirectAttributes.addAttribute("funcB", requestDto.getFunctionB());
        redirectAttributes.addAttribute("iteration", requestDto.getIteration());
        redirectAttributes.addAttribute("isOrderedOut", isOrderedOut);
        redirectAttributes.addAttribute("period", requestDto.getPeriod());
        return "redirect:/calc";
    }
}
