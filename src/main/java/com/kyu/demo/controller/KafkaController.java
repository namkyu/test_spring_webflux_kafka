package com.kyu.demo.controller;


import com.kyu.demo.model.Message;
import com.kyu.demo.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
public class KafkaController {

    private final MessageService messageService;

    @PostMapping
    public Mono<String> produceMessage(@RequestBody Mono<Message> message) {
        return message
                .flatMap(msg -> messageService.send(msg.getName(), msg));
    }

    /**
     *  일반적으로 하나의 request에 하나의 response가 전달되는 형태로 통신이 이루어진다.
     *  SSE(ServerSentEvent)에서 사용하는 event-stream 방식에서는 client가 request를 전송하면 connection이 맺어진 후에 half-duplex로 서버가 지속적으로 데이터를 보내줄 수 있다.
     *  SSE는 WebSocket 과 다르게 단방향이다. client <- server
     *
     * @return
     */
    @GetMapping
    public Flux<ServerSentEvent<Object>> consumeMessage() {
        return messageService.receive();
    }
}
