package com.example.bank_app_demo.service.impl;

import com.example.bank_app_demo.dto.*;
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

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService transactionService;

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

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("Account Creation")
                .messageBody("Dear " + savedUser.getFirstName() + " " + savedUser.getLastName() + ", your account has been created successfully. Your account number is " + savedUser.getAccountNumber())
                .build();

        emailService.sendEmailAlert(emailDetails);

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

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        boolean accountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if(!accountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(foundUser.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        boolean accountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if(!accountExists) {
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest creditDebitRequest) {
        boolean accountExists = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());
        if(!accountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());
        BigDecimal newBalance = userToCredit.getAccountBalance().add(creditDebitRequest.getAmount());
        userToCredit.setAccountBalance(newBalance);
        userRepository.save(userToCredit);

        TransactionDTO transactionDTO = TransactionDTO.builder()
                .transactionType("CREDIT")
                .amount(creditDebitRequest.getAmount())
                .accountNumber(userToCredit.getAccountNumber())
                .build();

        transactionService.saveTransaction(transactionDTO);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(newBalance)
                        .accountNumber(userToCredit.getAccountNumber())
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest creditDebitRequest) {
        boolean accountExists = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());
        if(!accountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());
        if(userToDebit.getAccountBalance().compareTo(creditDebitRequest.getAmount()) < 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_DEBITED)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_DEBITED_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        BigDecimal newBalance = userToDebit.getAccountBalance().subtract(creditDebitRequest.getAmount());
        userToDebit.setAccountBalance(newBalance);
        userRepository.save(userToDebit);

        TransactionDTO transactionDTO = TransactionDTO.builder()
                .transactionType("DEBIT")
                .amount(creditDebitRequest.getAmount())
                .accountNumber(userToDebit.getAccountNumber())
                .build();

        transactionService.saveTransaction(transactionDTO);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(newBalance)
                        .accountNumber(userToDebit.getAccountNumber())
                        .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName())
                        .build())
                .build();
    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        boolean sourceAccountExists = userRepository.existsByAccountNumber(request.getSourceAccountNumber());
        boolean destinationAccountExists = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if(!sourceAccountExists || !destinationAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User sourceUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        User destinationUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
        if(sourceUser.getAccountBalance().compareTo(request.getAmount()) < 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_DEBITED)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_DEBITED_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        BigDecimal newSourceBalance = sourceUser.getAccountBalance().subtract(request.getAmount());
        BigDecimal newDestinationBalance = destinationUser.getAccountBalance().add(request.getAmount());
        sourceUser.setAccountBalance(newSourceBalance);
        destinationUser.setAccountBalance(newDestinationBalance);
        userRepository.save(sourceUser);

        TransactionDTO transactionDTO = TransactionDTO.builder()
                .transactionType("DEBIT")
                .amount(request.getAmount())
                .accountNumber(destinationUser.getAccountNumber())
                .build();

        EmailDetails debitAlert = EmailDetails.builder()
                        .subject("Debit Alert")
                        .recipient(sourceUser.getEmail())
                        .messageBody("Dear " + sourceUser.getFirstName() + " " + sourceUser.getLastName() + ", your account has been debited with " + request.getAmount() + ". Your new balance is " + newSourceBalance)
                        .build();
        emailService.sendEmailAlert(debitAlert);
        userRepository.save(destinationUser);

        TransactionDTO transactionDTO2 = TransactionDTO.builder()
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .accountNumber(destinationUser.getAccountNumber())
                .build();

        EmailDetails creditAlert = EmailDetails.builder()
                .subject("Credit Alert")
                .recipient(destinationUser.getEmail())
                .messageBody("Dear " + destinationUser.getFirstName() + " " + destinationUser.getLastName() + ", your account has been credited with " + request.getAmount() + ". Your new balance is " + newDestinationBalance)
                .build();
        emailService.sendEmailAlert(creditAlert);
        transactionService.saveTransaction(transactionDTO);
        transactionService.saveTransaction(transactionDTO2);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_TRANSFER_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_TRANSFER_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(newSourceBalance)
                        .accountNumber(sourceUser.getAccountNumber())
                        .accountName(sourceUser.getFirstName() + " " + sourceUser.getLastName())
                        .build())
                .build();

    }
}
