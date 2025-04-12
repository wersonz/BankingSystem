package com.banking.entity;

import com.banking.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    @Id
    private UUID id;

    @NotNull(message = "交易类型不能为空")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @NotNull(message = "金额不能为空")
    @Positive(message = "金额必须大于0")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @NotNull(message = "账户ID不能为空")
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "related_account_id")
    private UUID relatedAccountId;

    @Size(max = 255, message = "描述长度不能超过255个字符")
    @Column(length = 255)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}