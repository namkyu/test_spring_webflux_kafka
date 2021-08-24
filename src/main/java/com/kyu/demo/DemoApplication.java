package com.kyu.demo;

import com.kyu.demo.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    private final MessageService messageService;

    @Bean
    public CommandLineRunner run() {
        return args -> {
        };
    }

    @PostConstruct
    public void init() {
        log.info("CPU : {}", Runtime.getRuntime().availableProcessors());
    }

}
