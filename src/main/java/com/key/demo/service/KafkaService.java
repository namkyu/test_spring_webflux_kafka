package com.key.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.KafkaSender;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaSender<String, Object> kafkaSender;
    private final ReceiverOptions<String, Object> receiverOptions;
    private final Sinks.Many<Object> sinksMany;
    private Disposable disposable;

    /**
     * subscribe 처리
     */
    @PostConstruct
    public void init() {
        disposable = KafkaReceiver.create(receiverOptions).receive()
                .doOnNext(processReceivedData())
                .doOnError(e -> {
                    System.out.println("Kafka read error");
                    init(); // reconnect kafka
                })
                .subscribe();
    }

    /**
     * 접속 종료 처리
     */
    @PreDestroy
    public void destroy() {
        if (disposable != null) {
            disposable.dispose();
        }
        kafkaSender.close();
    }

    /**
     * topic 으로 메세지 전송
     *
     * @param topic
     * @param key
     * @param value
     * @return
     */
    public Mono<Boolean> send(String topic, String key, Object value) {
        log.info("## (send) Current Thread : {}", Thread.currentThread().getName());
        return kafkaSender.createOutbound()
                .send(Mono.just(new ProducerRecord<>(topic, key, value)))
                .then()
                .map(ret -> true)
                .onErrorResume(e -> {
                    System.out.println("Kafka send error");
                    return Mono.just(false);
                });
    }

    /**
     * 수신된 데이터 처리
     *
     * @return
     */
    private Consumer<ReceiverRecord<String, Object>> processReceivedData() {
        return r -> {
            log.info("## (received) Current Thread : {}", Thread.currentThread().getName());
            Object receivedData = r.value();
            if (receivedData != null) {
                sinksMany.emitNext(r.value(), Sinks.EmitFailureHandler.FAIL_FAST);
            }
            r.receiverOffset().acknowledge();
        };
    }
}