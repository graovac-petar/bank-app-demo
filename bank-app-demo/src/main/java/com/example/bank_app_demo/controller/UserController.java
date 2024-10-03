package com.example.bank_app_demo.controller;

import com.example.bank_app_demo.dto.BankResponse;
import com.example.bank_app_demo.dto.UserRequestDTO;
import com.example.bank_app_demo.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequestDTO userRequestDTO) {
        return userService.createAccount(userRequestDTO);
    }
}
