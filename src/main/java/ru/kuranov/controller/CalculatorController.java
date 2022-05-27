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

    @GetMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Object> streamDataFlux(@RequestParam String funcA,
                                       @RequestParam String funcB,
                                       @RequestParam int iteration,
                                       @RequestParam long period,
                                       @RequestParam boolean isOrderedOut) throws InterruptedException, ExecutionException {

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
            startA.setRelease(System.nanoTime());

            if (countA.get() < iteration) {

//                try {
//                    Thread.sleep(30L);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                try {
                    // вычисление функции
                    calcA.setRelease(calculator.calc(funcA, (int) countA.get()));
                } catch (ScriptException | NoSuchMethodException | IOException | URISyntaxException e) {
                    e.printStackTrace();
                }

                // прибавляем инкремент
                countA.getAndIncrement();
            }

            // засекаем время окончания расчета
            stopA.setRelease(System.nanoTime());
            timesA.getAndIncrement();

            // собираем и сохраняем результирующую строку
            resultA.setRelease("<function-A>,<" + calcA.get() + ">,<" + (stopA.get() - startA.get()) + " ns>");
        };


        // поток для расчета второй функции
        Runnable taskB = () -> {

            // засекаем время начала расчета
            startB.setRelease(System.nanoTime());

            if (countB.get() < iteration) {

//                try {
//                    Thread.sleep(80L);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                try {
                    calcB.setRelease(calculator.calc(funcB, (int) countB.get()));
                } catch (ScriptException | NoSuchMethodException | IOException | URISyntaxException e) {
                    e.printStackTrace();
                }

                // прибавляем инкремент
                countB.getAndIncrement();
            }

            // засекаем время окончания расчета
            stopB.setRelease(System.nanoTime());
            timesB.getAndIncrement();

            // собираем и сохраняем результирующую строку
            resultB.setRelease("<function-B>,<" + calcB.get() + ">,<" + (stopB.get() - startB.get()) + " ns>");
        };

        // запуск ExecutorService с минимальными задержками
        ScheduledFuture<?> scheduledFutureA = serviceA.scheduleAtFixedRate(taskA, 1, 1L, TimeUnit.NANOSECONDS);
        ScheduledFuture<?> scheduledFutureB = serviceB.scheduleAtFixedRate(taskB, 1, 1L, TimeUnit.NANOSECONDS);

        while (resultA.get().equals("") || resultB.get().equals("")) {
            // ожидаем первые результаты расчетов функций
        }

        return Flux
                .range(1, iteration)
                .delaySequence(Duration.ofMillis(period))
                .map(i -> {

                    // останавливаем поток А
                    if (countA.get() >= iteration) {
                        scheduledFutureA.cancel(true);
                        serviceA.shutdown();
                        countA.setRelease(0L);
                    }

                    // останавливаем поток В
                    if (countB.get() >= iteration) {
                        scheduledFutureB.cancel(true);
                        serviceB.shutdown();
                        countB.setRelease(0L);
                    }

                    // выводим результат в звисимости от требований клиента
                    if (isOrderedOut) {
                        return "<" + i + ">," + resultA.get() + ",<" + timesA.get() + " times>,\t" + "<" + i + ">," + resultB.get() + ",<" + timesB.get() + " times>";
                    } else {
                        return "<" + i + ">," + resultA.get() + "\n" + "<" + i + ">," + resultB.get();
                    }
                });
    }
}