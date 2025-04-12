package com.banking.exception;

import java.util.UUID;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(UUID id) {
        super("未找到ID为 " + id + " 的交易");
    }
} 