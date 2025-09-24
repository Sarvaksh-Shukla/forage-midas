package com.jpmc.midascore.entity;

import jakarta.persistence.*;


@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private float amount;
    @Column(nullable = false)
    private float incentiveAmount;


    @ManyToOne
    @JoinColumn(name = "sender_id",nullable = false)
    private UserRecord sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id",nullable = false)
    private UserRecord recipient;

    
     protected TransactionRecord() {


     }

        public TransactionRecord(UserRecord sender, UserRecord recipient,float amount, float incentiveAmount) {
        this.amount = amount;
        this.sender = sender;
        this.recipient = recipient;
        this.incentiveAmount = incentiveAmount;

         }

         public Long getId() {
             return id;
    }

         public float getAmount() {
             return amount;
         }

         public UserRecord getSender() {
             return sender;
         }

         public UserRecord getRecipient() {
             return recipient;
         }
        public float getIncentiveAmount() {
             return incentiveAmount;
         }



        @Override
        public String toString() {
        return "TransactionRecord{" +
                "id=" + id +
                ", amount=" + amount +
                ", sender=" + (sender != null ? sender.getName() : null) +
                ", recipient=" + (recipient != null ? recipient.getName() : null) +
                ", Balance of "+sender.getName()+"="+ (sender != null ? sender.getBalance() : null) +
                ", Balance of "+recipient.getName()+"=" + (recipient != null ? recipient.getBalance() : null) +
                ", incentiveAmount=" + incentiveAmount +
                '}'; 

        
    }   


}
