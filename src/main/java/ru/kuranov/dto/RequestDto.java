package ru.kuranov.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {

    private String functionA;

    private String functionB;

    private String period;

    private String iteration;
}
