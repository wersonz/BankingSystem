package com.banking.dto;

import com.banking.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionDTO {
    @NotNull(message = "交易ID不能为空")
    private UUID id;

    @NotNull(message = "交易类型不能为空")
    private TransactionType type;

    @NotNull(message = "金额不能为空")
    @Positive(message = "金额必须大于0")
    private BigDecimal amount;

    @NotNull(message = "账户ID不能为空")
    private UUID accountId;

    private UUID relatedAccountId;

    @Size(max = 255, message = "描述长度不能超过255个字符")
    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
} 