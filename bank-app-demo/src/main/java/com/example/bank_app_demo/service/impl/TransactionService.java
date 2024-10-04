package com.example.bank_app_demo.service.impl;

import com.example.bank_app_demo.dto.TransactionDTO;
import com.example.bank_app_demo.entity.Transaction;

import java.math.BigDecimal;

public interface TransactionService {
    void saveTransaction(TransactionDTO transaction);
}
