package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;


@Component
public class TransactionListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionListener.class);
    private final UserRepository userRepository;
   
    private final TransactionRepository transactionRepository;

    public TransactionListener(UserRepository userRepository,  TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
    
        this.transactionRepository = transactionRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic}")
    public void listen(Transaction tx) {
        //log.info("Received transaction: {}", tx);
    
      UserRecord sender = userRepository.findById(tx.getSenderId());

        // Validate Sender
        if (sender == null) {
            System.out.println("Sender not found: " + tx.getSenderId());
            return;
        }

        UserRecord recipient = userRepository.findById(tx.getRecipientId());
        
        // Validate Recipient
        if (recipient == null) {
            System.out.println("Recipient not found: " + tx.getRecipientId());
            return;
        }

        // Validate sender has enough to cover transaction
        if (sender.getBalance() < tx.getAmount()) {
            System.out.println("Insufficient balance for sender: " + tx.getSenderId());
            return;
        }
         sender.setBalance(sender.getBalance() - tx.getAmount());
        recipient.setBalance(recipient.getBalance() + tx.getAmount());

        userRepository.save(sender);
        userRepository.save(recipient);


        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, tx.getAmount());
        transactionRepository.save(transactionRecord);
        log.info("Senderinfo:    sender :  {}  balance : {}", sender.getName(),sender.getBalance());
        log.info("Recipientinfo: recipient:{}  balance : {}", recipient.getName(),recipient.getBalance());
        log.info( transactionRecord.toString());

}
}