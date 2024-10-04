package com.example.bank_app_demo.utils;

import java.time.Year;

public class AccountUtils {
    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "Account already exists";
    public static final String ACCOUNT_CREATION_SUCCESS = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account created successfully";
    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "User with provided Account Number does not exist";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_SUCCESS = "User Account Found";
    public static final String ACCOUNT_CREDITED_SUCCESS = "005";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "User Account Credited";
    public static final String ACCOUNT_NOT_DEBITED = "006";
    public static final String ACCOUNT_NOT_DEBITED_MESSAGE = "Insufficient funds to debit";
    public static final String ACCOUNT_DEBITED_SUCCESS = "007";
    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "User Account DEBITED";


    public static String generateAccountNumber() {
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;
        int random_int = (int)(Math.random() * (max - min + 1) + min);
        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(random_int);
        StringBuilder accountNumber = new StringBuilder();
        return accountNumber.append(year).append(randomNumber).toString();
    }
}
