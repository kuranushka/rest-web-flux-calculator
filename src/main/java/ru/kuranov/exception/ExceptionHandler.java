package ru.kuranov.exception;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import ru.kuranov.dto.RequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static ru.kuranov.error.Errors.FUNCTION_ERRORS;

@Component
public class ExceptionHandler implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("functionError", FUNCTION_ERRORS);

        // возвращаем клиенту заполненные им данные для исправления
        RequestDto requestDto = RequestDto.builder()
                .functionA(request.getParameter("functionA"))
                .functionB(request.getParameter("functionB"))
                .iteration(request.getParameter("iteration"))
                .period(request.getParameter("period"))
                .pauseA(request.getParameter("pauseA"))
                .pauseB(request.getParameter("pauseB"))
                .build();
        map.put("requestDto", requestDto);
        return new ModelAndView("page", map);
    }
}
