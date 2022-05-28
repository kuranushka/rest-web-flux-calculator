package ru.kuranov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.kuranov.service.Calculator;

import javax.script.ScriptException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/calc")
@RequiredArgsConstructor
public class CalculatorController {

    private final Calculator calculator;
    private final String DELIMITER = "\t";
    private final String RETURN_CARR = "\n";

    @GetMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Object> streamDataFlux(@RequestParam String funcA,
                                       @RequestParam String funcB,
                                       @RequestParam int iteration,
                                       @RequestParam long period,
                                       @RequestParam boolean isOrderedOut,
                                       @RequestParam long pauseA,
                                       @RequestParam long pauseB) {

        // переменные для подсчета количества итераций
        AtomicLong timesA = new AtomicLong(0L);
        AtomicLong timesB = new AtomicLong(0L);

        // переменный для итерирования
        AtomicLong countA = new AtomicLong(0L);
        AtomicLong countB = new AtomicLong(0L);

        // переменные для измерения времени работы функций
        AtomicLong startA = new AtomicLong(0L);
        AtomicLong stopA = new AtomicLong(0L);
        AtomicLong startB = new AtomicLong(0L);
        AtomicLong stopB = new AtomicLong(0L);

        // переменные для сохранения результата вычисления функций
        AtomicReference<String> calcA = new AtomicReference<>("");
        AtomicReference<String> calcB = new AtomicReference<>("");

        // переменные для хранения результирующих строк
        AtomicReference<String> resultA = new AtomicReference<>("");
        AtomicReference<String> resultB = new AtomicReference<>("");

        // открываем ExecutorService
        ScheduledExecutorService serviceA = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService serviceB = Executors.newScheduledThreadPool(1);

        // поток для расчета первой функции
        Runnable taskA = () -> {

            // засекаем время начала расчета
            startA.setPlain(System.nanoTime());

            // если не достигли предела итерации, то вычисляем функцию
            if (countA.get() < iteration) {

                try {
                    // вычисление функции А
                    calcA.setPlain(calculator.calc(funcA, (int) countA.get()));
                } catch (ScriptException | NoSuchMethodException | IOException | URISyntaxException e) {
                    e.printStackTrace();
                }

                // прибавляем инкремент
                countA.getAndIncrement();
            }

            // засекаем время окончания расчета
            stopA.setPlain(System.nanoTime());
            timesA.getAndIncrement();

            // собираем и сохраняем результирующую строку
            if (timesA.get() <= iteration) {
                resultA.setPlain(String.format("<function-A>%s<%s>%s%s<%d>",
                        DELIMITER, calcA.get(), DELIMITER, DELIMITER, (stopA.get() - startA.get())));
            } else {

                // если функция закончила свою работу
                resultA.setPlain(String.format("<A-completed>%s<--->%s%s<------>",
                        DELIMITER, DELIMITER, DELIMITER));
            }
        };


        // поток для расчета второй функции
        Runnable taskB = () -> {

            // засекаем время начала расчета
            startB.setPlain(System.nanoTime());

            // если не достигли предела итерации, то вычисляем функцию
            if (countB.get() < iteration) {

                try {
                    // вычисление функции B
                    calcB.setPlain(calculator.calc(funcB, (int) countB.get()));
                } catch (ScriptException | NoSuchMethodException | IOException | URISyntaxException e) {
                    e.printStackTrace();
                }

                // прибавляем инкремент
                countB.getAndIncrement();
            }

            // засекаем время окончания расчета
            stopB.setPlain(System.nanoTime());
            timesB.getAndIncrement();

            // собираем и сохраняем результирующую строку
            if (timesB.get() <= iteration) {
                resultB.setPlain(String.format("<function-B>%s<%s>%s%s<%d>",
                        DELIMITER, calcB.get(), DELIMITER, DELIMITER, (stopB.get() - startB.get())));
            } else {

                // если функция закончила свою работу
                resultB.setPlain(String.format("<B-completed>%s<--->%s%s<------>",
                        DELIMITER, DELIMITER, DELIMITER));
            }
        };

        // запуск ExecutorService с минимальными задержками и регулируемыми паузами между перезапуском потоков (минимальная пауза между перезапуском потоков = 1)
        ScheduledFuture<?> scheduledFutureA = serviceA.scheduleAtFixedRate(taskA, 1, pauseA > 0 ? pauseA : 1, TimeUnit.MILLISECONDS);
        ScheduledFuture<?> scheduledFutureB = serviceB.scheduleAtFixedRate(taskB, 1, pauseB > 0 ? pauseB : 1, TimeUnit.MILLISECONDS);

        while (resultA.get().equals("") || resultB.get().equals("")) {
            // ожидаем первые результаты расчетов функций в этом цикле
        }


        return Flux
                .range(0, iteration)
                .delaySequence(Duration.ofMillis(period))
                .map(i -> {

                    // останавливаем поток А
                    if (countA.get() >= iteration) {
                        scheduledFutureA.cancel(true);
                        serviceA.shutdownNow();
                    }

                    // останавливаем поток В
                    if (countB.get() >= iteration) {
                        scheduledFutureB.cancel(true);
                        serviceB.shutdownNow();
                    }


                    // выводим первую строку с заголовками
                    if (i == 0) {

                        // для упорядоченного вывода
                        if (isOrderedOut) {
                            return String.format("<№>%s<функция>%s<результат>%s<время, ns>%s<рез. наперед>%s<функция>%s<результат>%s<время>%s%s<рез. наперед>",
                                    DELIMITER, DELIMITER, DELIMITER, DELIMITER, DELIMITER, DELIMITER, DELIMITER, DELIMITER, DELIMITER);
                        } else {

                            //  для неупорядоченного вывода
                            return String.format("<№>%s<функция>%s<результат>%s<время, ns>",
                                    DELIMITER, DELIMITER, DELIMITER);
                        }
                    }


                    // выводим результат в зависимости от требований клиента
                    if (isOrderedOut) {
                        return String.format("<%d>%s%s%s<%d>%s%s%s%s<%d>",
                                i, DELIMITER, resultA.get(), DELIMITER, timesA.get(), DELIMITER, DELIMITER, resultB.get(), DELIMITER, timesB.get());
                    } else {
                        return String.format("<%d>%s%s%s<%d>%s%s",
                                i, DELIMITER, resultA.get(), RETURN_CARR, i, DELIMITER, resultB.get());
                    }
                });
    }
}