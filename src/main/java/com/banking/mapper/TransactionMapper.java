package com.banking.mapper;

import com.banking.dto.TransactionDTO;
import com.banking.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionDTO toDTO(Transaction entity) {
        if (entity == null) {
            return null;
        }

        TransactionDTO dto = new TransactionDTO();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setAmount(entity.getAmount());
        dto.setAccountId(entity.getAccountId());
        dto.setRelatedAccountId(entity.getRelatedAccountId());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public Transaction toEntity(TransactionDTO dto) {
        if (dto == null) {
            return null;
        }

        Transaction entity = new Transaction();
        entity.setId(dto.getId());
        entity.setType(dto.getType());
        entity.setAmount(dto.getAmount());
        entity.setAccountId(dto.getAccountId());
        entity.setRelatedAccountId(dto.getRelatedAccountId());
        entity.setDescription(dto.getDescription());
        // 不设置时间字段，让@PrePersist和@PreUpdate处理
        return entity;
    }
} 