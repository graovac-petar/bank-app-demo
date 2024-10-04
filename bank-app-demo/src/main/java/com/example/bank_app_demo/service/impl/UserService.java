package com.example.bank_app_demo.service.impl;

import com.example.bank_app_demo.dto.*;

public interface UserService {
    BankResponse createAccount(UserRequestDTO userRequestDTO);
    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
    String nameEnquiry(EnquiryRequest enquiryRequest);
    BankResponse creditAccount(CreditDebitRequest creditDebitRequest);
    BankResponse debitAccount(CreditDebitRequest creditDebitRequest);
    BankResponse transfer(TransferRequest request);
}
