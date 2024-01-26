package com.shop.config;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Log4j2
@Aspect
@Component
public class MainAOP {
    @Before("execution(* *..*Controller.*(..))")
    public void logBefore(JoinPoint joinPoint){
        log.warn("메소드 실행:" + joinPoint.getSignature().getName());
        Arrays.stream(joinPoint.getArgs()).parallel()
                .forEach(log::warn);
    }
}
