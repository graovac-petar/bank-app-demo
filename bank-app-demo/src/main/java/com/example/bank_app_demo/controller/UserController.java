package com.example.bank_app_demo.controller;

import com.example.bank_app_demo.dto.BankResponse;
import com.example.bank_app_demo.dto.CreditDebitRequest;
import com.example.bank_app_demo.dto.EnquiryRequest;
import com.example.bank_app_demo.dto.UserRequestDTO;
import com.example.bank_app_demo.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequestDTO userRequestDTO) {
        return userService.createAccount(userRequestDTO);
    }

    @GetMapping("/balance")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request) {
        return userService.balanceEnquiry(request);
    }

    @GetMapping("/name")
    public String nameEnquiry(@RequestBody EnquiryRequest request) {
        return userService.nameEnquiry(request);
    }

    @PostMapping("/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request) {
        return userService.creditAccount(request);
    }

    @PostMapping("/debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request) {
        return userService.debitAccount(request);
    }
}
