package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionListener.class);

    @KafkaListener(topics = "${general.kafka-topic}", containerFactory = "kafkaListenerContainerFactory",groupId = "midas-group")
    public void listen(Transaction tx) {
        log.info("Received transaction: {}", tx);
    }
}