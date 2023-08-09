package com.anczykowski.assigner.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Setter
public class ErrorResponseEntity {
    private LocalDateTime timestamp = LocalDateTime.now();

    private final String message;

    private final String description;

}
