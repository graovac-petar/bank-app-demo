package com.example.bank_app_demo.service.impl;

import com.example.bank_app_demo.dto.BankResponse;
import com.example.bank_app_demo.dto.UserRequestDTO;

public interface UserService {
    BankResponse createAccount(UserRequestDTO userRequestDTO);
}
