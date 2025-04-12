package com.banking.enums;

/**
 * 交易类型枚举
 */
public enum TransactionType {
    /**
     * 存款
     */
    DEPOSIT,

    /**
     * 取款
     */
    WITHDRAWAL,

    /**
     * 转账-转出
     */
    TRANSFER_OUT,

    /**
     * 转账-转入
     */
    TRANSFER_IN
} 