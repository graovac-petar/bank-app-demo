package com.example.bank_app_demo.service.impl;

import com.example.bank_app_demo.dto.BankResponse;
import com.example.bank_app_demo.dto.CreditDebitRequest;
import com.example.bank_app_demo.dto.EnquiryRequest;
import com.example.bank_app_demo.dto.UserRequestDTO;

public interface UserService {
    BankResponse createAccount(UserRequestDTO userRequestDTO);
    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
    String nameEnquiry(EnquiryRequest enquiryRequest);
    BankResponse creditAccount(CreditDebitRequest creditDebitRequest);
    BankResponse debitAccount(CreditDebitRequest creditDebitRequest);
}
