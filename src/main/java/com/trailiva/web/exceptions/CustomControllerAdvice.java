package com.trailiva.web.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;


@ControllerAdvice(basePackages = {"com.trailiva.security"})
@Slf4j
public class CustomControllerAdvice {
}
