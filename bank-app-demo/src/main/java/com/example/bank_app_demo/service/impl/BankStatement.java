package com.example.bank_app_demo.service.impl;

import com.example.bank_app_demo.dto.EmailDetails;
import com.example.bank_app_demo.entity.Transaction;
import com.example.bank_app_demo.entity.User;
import com.example.bank_app_demo.repository.TransactionRepository;
import com.example.bank_app_demo.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
@Service
@AllArgsConstructor
@Slf4j
public class BankStatement {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    private static final String FILE = "C:\\Users\\38161\\Downloads\\bank-app-demo\\statement.pdf";

    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        List<Transaction> transactionsList = transactionRepository.findAll().stream()
                .filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> transaction.getCreatedAt().isAfter(start) || transaction.getCreatedAt().isEqual(start))
                .filter(transaction -> transaction.getCreatedAt().isBefore(end) || transaction.getCreatedAt().isEqual(end))
                .toList();

        User user=userRepository.findByAccountNumber(accountNumber);
        String customerName = user.getFirstName() + " " + user.getLastName()+ " " + user.getOtherName();

        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        log.info("Setting size of the document to A4");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document, outputStream);
        document.open();
        log.info("Opening the document");
        PdfPTable bankInfo = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("MyBank "));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.BLUE);
        bankName.setPadding(20f);

        PdfPCell bankAddress = new PdfPCell(new Phrase("Batajnica, Belgrade"));
        bankAddress.setBorder(0);
        bankInfo.addCell(bankName);
        bankInfo.addCell(bankAddress);

        PdfPTable statementInfo = new PdfPTable(2);
        PdfPCell startOfDate = new PdfPCell(new Phrase("Start Date: " + startDate));
        startOfDate.setBorder(0);
        PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT" ));
        statement.setBorder(0);
        PdfPCell stopOfDate = new PdfPCell(new Phrase("End Date: " + endDate));
        stopOfDate.setBorder(0);

        PdfPCell name = new PdfPCell(new Phrase("Customer Name: " + customerName));
        name.setBorder(0);

        PdfPCell space = new PdfPCell();
        space.setBorder(0);

        PdfPCell adress = new PdfPCell(new Phrase("Customer Address: " + user.getAddress()));
        adress.setBorder(0);

        PdfPTable transactionTable = new PdfPTable(4);
        PdfPCell date=new PdfPCell(new Phrase("Date"));
        date.setBackgroundColor(BaseColor.BLUE);
        date.setBorder(0);

        PdfPCell transactionType = new PdfPCell(new Phrase("Transaction Type"));
        transactionType.setBackgroundColor(BaseColor.BLUE);
        transactionType.setBorder(0);

        PdfPCell transactionAmount = new PdfPCell(new Phrase("Transaction Amount"));
        transactionAmount.setBackgroundColor(BaseColor.BLUE);
        transactionAmount.setBorder(0);

        PdfPCell status = new PdfPCell(new Phrase("Status"));
        status.setBackgroundColor(BaseColor.BLUE);
        status.setBorder(0);

        transactionTable.addCell(date);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(transactionAmount);
        transactionTable.addCell(status);

        transactionsList.forEach(transaction -> {
            transactionTable.addCell((new Phrase(transaction.getCreatedAt().toString())));
            transactionTable.addCell((new Phrase(transaction.getTransactionType())));
            transactionTable.addCell((new Phrase(transaction.getAmount().toString())));
            transactionTable.addCell((new Phrase(transaction.getStatus())));
        });

        statementInfo.addCell(startOfDate);
        statementInfo.addCell(statement);
        statementInfo.addCell(stopOfDate);
        statementInfo.addCell(name);
        statementInfo.addCell(space);
        statementInfo.addCell(adress);

        document.add(bankInfo);
        document.add(statementInfo);
        document.add(transactionTable);
        document.close();

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .messageBody("Kindly find your requested account statement attached")
                .subject("STATEMENT OF ACCOUNT")
                .attachment(FILE)
                .build();

        emailService.sendEmailWithAttachment(emailDetails);

        return transactionsList;
    }
}
