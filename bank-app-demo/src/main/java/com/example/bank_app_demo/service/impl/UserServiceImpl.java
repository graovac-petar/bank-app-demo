package com.example.bank_app_demo.service.impl;

import com.example.bank_app_demo.dto.AccountInfo;
import com.example.bank_app_demo.dto.BankResponse;
import com.example.bank_app_demo.dto.UserRequestDTO;
import com.example.bank_app_demo.entity.User;
import com.example.bank_app_demo.repository.UserRepository;
import com.example.bank_app_demo.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Override
    public BankResponse createAccount(UserRequestDTO userRequestDTO) {
        //Creating an account - saving to the database

        if(userRepository.existsByEmail(userRequestDTO.getEmail())){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User newUser = User.builder()
                .firstName(userRequestDTO.getFirstName())
                .lastName(userRequestDTO.getLastName())
                .otherName(userRequestDTO.getOtherName())
                .gender(userRequestDTO.getGender())
                .address(userRequestDTO.getAddress())
                .stateOfOrigin(userRequestDTO.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequestDTO.getEmail())
                .phoneNumber(userRequestDTO.getPhoneNumber())
                .alternativePhoneNumber(userRequestDTO.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName())
                        .build())
                .build();
    }
}
