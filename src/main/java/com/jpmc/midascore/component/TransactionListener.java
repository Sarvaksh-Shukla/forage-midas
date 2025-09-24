package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.foundation.Incentive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;


@Component
public class TransactionListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionListener.class);
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    public TransactionListener(UserRepository userRepository,  TransactionRepository transactionRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.transactionRepository = transactionRepository;

    }

    @KafkaListener(topics = "${general.kafka-topic}")
    @Transactional
    public void listen(Transaction tx) {
        log.info("Received transaction: {}", tx);
        
        
      UserRecord sender = userRepository.findById(tx.getSenderId());

        // Validate Sender
        if (sender == null) {
            log.info("Sender not found: {} " + tx.getSenderId());
            return;
        }

        UserRecord recipient = userRepository.findById(tx.getRecipientId());
        
        // Validate Recipient
        if (recipient == null) {
            log.info("Recipient not found: {}" + tx.getRecipientId());
            return;
        }

        // Validate sender has enough to cover transaction
        if (sender.getBalance() < tx.getAmount()) {
            log.info("Insufficient balance for sender: {} " + tx.getSenderId());
            return;
        }

        
        Incentive incentive = null;
        try {
            incentive = restTemplate.postForObject("http://localhost:8081/incentive", tx, Incentive.class);
        } catch (Exception e) {
            log.info("Error fetching incentive: {}" + e.getMessage());
            return;
        }

        float incentiveAmount = incentive != null ? incentive.getAmount() : 0.0f;

        // Process Transaction  
        sender.setBalance(sender.getBalance() - tx.getAmount());
        recipient.setBalance(recipient.getBalance() + tx.getAmount() + incentiveAmount);

        userRepository.save(sender);
        userRepository.save(recipient);
       
       
        // Log Transaction  
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, tx.getAmount(), incentiveAmount);
        transactionRepository.save(transactionRecord);



        log.info("Senderinfo:    sender :  {}  balance : {}", sender.getName(),sender.getBalance());
        log.info("Recipientinfo: recipient:{}  balance : {}", recipient.getName(),recipient.getBalance());
        log.info( transactionRecord.toString());
        log.info("Incentive applied: {}", incentiveAmount);
        
        log.info("Balance of "+recipient.getName()+" = { } "+userRepository.findById(9).getBalance());

    }
}