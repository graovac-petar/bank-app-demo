package com.example.bank_app_demo.service.impl;

import com.example.bank_app_demo.dto.TransactionDTO;
import com.example.bank_app_demo.entity.Transaction;
import com.example.bank_app_demo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionServiceImpl implements TransactionService{
    @Autowired
    TransactionRepository transactionRepository;
    @Override
    public void saveTransaction(TransactionDTO transaction) {
        Transaction transactionEntity = Transaction.builder()
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .accountNumber(transaction.getAccountNumber())
                .status("SUCCESS")
                .build();
        transactionRepository.save(transactionEntity);
        System.out.println("Transaction saved successfully");
    }
}
