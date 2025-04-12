package com.banking.service;

import com.banking.dto.TransactionDTO;

import java.util.List;
import java.util.UUID;

public interface TransactionService {
    TransactionDTO createTransaction(TransactionDTO transactionDTO);

    TransactionDTO getTransaction(UUID id);

    List<TransactionDTO> transactionList(int page, int size);

    TransactionDTO updateTransaction(UUID id, TransactionDTO transactionDTO);

    void deleteTransaction(UUID id);
} 