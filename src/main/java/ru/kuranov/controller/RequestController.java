package ru.kuranov.controller;

import lombok.RequiredArgsConstructor;
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
import static ru.kuranov.value.DefaultValues.*;

@Controller
@RequiredArgsConstructor
public class RequestController {

    private final Calculator calculator;
    private final Validator validator;

    @GetMapping
    public String handleGet(Model model) {

        // передаём DTO на view для трансфера данных
        RequestDto requestDto = RequestDto.builder()
                .functionA(DEFAULT_FUNCTION.toString())
                .functionB(DEFAULT_FUNCTION.toString())
                .period(DEFAULT_PERIOD.toString())
                .iteration(DEFAULT_ITERATION.toString())
                .pauseA(DEFAULT_PAUSE.toString())
                .pauseB(DEFAULT_PAUSE.toString())
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
        if (!validator.isValid(requestDto.getIteration())) {
            model.addAttribute("errorIteration", USE_ONLY_DIGITS_MINIMUM_ITERATION_ONE);
            return "page";
        }

        // валидируем период итераций
        if (!validator.isValid(requestDto.getPeriod())) {
            model.addAttribute("errorPeriod", USE_ONLY_DIGITS_AND_MINIMUM_PERIOD_ONE);
            return "page";
        }

        // валидируем паузу функции А
        if (!validator.isValidPause(requestDto.getPauseA())) {
            model.addAttribute("errorPauseA", USE_ONLY_DIGITS);
            return "page";
        }

        // валидируем паузу функции B
        if (!validator.isValidPause(requestDto.getPauseB())) {
            model.addAttribute("errorPauseB", USE_ONLY_DIGITS);
            return "page";
        }

        // перенаправляем данные в WebFlux REST
        redirectAttributes.addAttribute("funcA", requestDto.getFunctionA());
        redirectAttributes.addAttribute("funcB", requestDto.getFunctionB());
        redirectAttributes.addAttribute("iteration", requestDto.getIteration());
        redirectAttributes.addAttribute("isOrderedOut", isOrderedOut);
        redirectAttributes.addAttribute("period", requestDto.getPeriod());
        redirectAttributes.addAttribute("pauseA", requestDto.getPauseA());
        redirectAttributes.addAttribute("pauseB", requestDto.getPauseB());
        return "redirect:/calc";
    }
}
