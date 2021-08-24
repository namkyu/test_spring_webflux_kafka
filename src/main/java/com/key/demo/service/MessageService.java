package com.key.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.key.demo.exception.KafkaException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final Sinks.Many<Object> sinksMany;
    private final KafkaService kafkaService;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic}")
    private String topic;

    public Mono<String> send(String key, Object value) {
        try {
            return kafkaService.send(topic, key, objectMapper.writeValueAsString(value))
                    .map(b -> (b) ? "success send message" : "fail send message");
        } catch (JsonProcessingException e) {
            return Mono.error(KafkaException.SEND_ERROR);
        }
    }

    public Flux<ServerSentEvent<Object>> receive() {
        return sinksMany
                .asFlux()
                .publishOn(Schedulers.parallel())
                .map(message -> ServerSentEvent.builder(message).build())
                .mergeWith(ping())
                .onErrorResume(e -> Flux.empty())
                .doOnCancel(() -> System.out.println("disconnected by client"));
    }

    private Flux<ServerSentEvent<Object>> ping() {
        return Flux.interval(Duration.ofMillis(5000))
                .map(i -> ServerSentEvent.builder().build());
    }
}