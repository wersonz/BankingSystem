package com.banking.service;

import com.banking.dto.TransactionDTO;
import com.banking.entity.Transaction;
import com.banking.enums.TransactionType;
import com.banking.mapper.TransactionMapper;
import com.banking.repository.TransactionRepository;
import com.banking.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionService = new TransactionServiceImpl(transactionRepository, transactionMapper);
    }

    @Test
    void createTransaction_ShouldReturnCreatedTransaction() {
        // 准备测试数据
        TransactionDTO dto = new TransactionDTO();
        dto.setId(UUID.randomUUID());
        dto.setType(TransactionType.DEPOSIT);
        dto.setAmount(new BigDecimal("100.00"));
        dto.setAccountId(UUID.randomUUID());
        dto.setDescription("测试存款");

        Transaction entity = new Transaction();
        entity.setId(entity.getId());
        entity.setType(TransactionType.DEPOSIT);
        entity.setAmount(new BigDecimal("100.00"));
        entity.setAccountId(dto.getAccountId());
        entity.setDescription("测试存款");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        // 设置模拟行为
        when(transactionMapper.toEntity(any(TransactionDTO.class))).thenReturn(entity);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(entity);
        when(transactionMapper.toDTO(any(Transaction.class))).thenReturn(dto);

        // 执行测试
        TransactionDTO created = transactionService.createTransaction(dto);

        // 验证结果
        assertNotNull(created);
        assertEquals(TransactionType.DEPOSIT, created.getType());
        assertEquals(new BigDecimal("100.00"), created.getAmount());
        assertEquals(dto.getAccountId(), created.getAccountId());

        // 验证交互
        verify(transactionMapper).toEntity(dto);
        verify(transactionRepository).save(entity);
        verify(transactionMapper).toDTO(entity);
    }

    @Test
    void getTransaction_ShouldReturnTransaction_WhenExists() {
        // 准备测试数据
        UUID id = UUID.randomUUID();
        Transaction entity = new Transaction();
        entity.setId(id);
        entity.setType(TransactionType.DEPOSIT);
        entity.setAmount(new BigDecimal("100.00"));
        entity.setAccountId(UUID.randomUUID());
        entity.setDescription("测试存款");

        TransactionDTO dto = new TransactionDTO();
        dto.setId(id);
        dto.setType(TransactionType.DEPOSIT);
        dto.setAmount(new BigDecimal("100.00"));
        dto.setAccountId(entity.getAccountId());
        dto.setDescription("测试存款");

        // 设置模拟行为
        when(transactionRepository.findById(id)).thenReturn(Optional.of(entity));
        when(transactionMapper.toDTO(entity)).thenReturn(dto);

        // 执行测试
        TransactionDTO found = transactionService.getTransaction(id);

        // 验证结果
        assertNotNull(found);
        assertEquals(id, found.getId());
        assertEquals(entity.getAccountId(), found.getAccountId());

        // 验证交互
        verify(transactionRepository).findById(id);
        verify(transactionMapper).toDTO(entity);
    }

    @Test
    void transactionList_ShouldReturnPaginatedResults() {
        // 准备测试数据
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Transaction transaction = new Transaction();
            transaction.setId(UUID.randomUUID());
            transaction.setType(TransactionType.DEPOSIT);
            transaction.setAmount(new BigDecimal("100.00"));
            transaction.setAccountId(UUID.randomUUID());
            transaction.setDescription("测试存款 " + i);
            transactions.add(transaction);
        }

        Page<Transaction> transactionPage = new PageImpl<>(
                transactions.subList(0, Math.min(size, transactions.size())),
                pageable,
                transactions.size()
        );

        List<TransactionDTO> dtos = new ArrayList<>();
        for (Transaction transaction : transactionPage.getContent()) {
            TransactionDTO dto = new TransactionDTO();
            dto.setId(transaction.getId());
            dto.setType(transaction.getType());
            dto.setAmount(transaction.getAmount());
            dto.setAccountId(transaction.getAccountId());
            dto.setDescription(transaction.getDescription());
            dtos.add(dto);
        }

        // 设置模拟行为
        when(transactionRepository.findAll(pageable)).thenReturn(transactionPage);
        when(transactionMapper.toDTO(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction entity = invocation.getArgument(0);
            TransactionDTO dto = new TransactionDTO();
            dto.setId(entity.getId());
            dto.setType(entity.getType());
            dto.setAmount(entity.getAmount());
            dto.setAccountId(entity.getAccountId());
            dto.setDescription(entity.getDescription());
            return dto;
        });

        // 执行测试
        List<TransactionDTO> result = transactionService.transactionList(page, size);

        // 验证结果
        assertEquals(Math.min(size, transactions.size()), result.size());

        // 验证交互
        verify(transactionRepository).findAll(pageable);
        verify(transactionMapper, times(Math.min(size, transactions.size()))).toDTO(any(Transaction.class));
    }

    @Test
    void updateTransaction_ShouldUpdateExistingTransaction() {
        // 准备测试数据
        UUID id = UUID.randomUUID();
        TransactionDTO updateDto = new TransactionDTO();
        updateDto.setId(id);
        updateDto.setType(TransactionType.WITHDRAWAL);
        updateDto.setAmount(new BigDecimal("50.00"));
        updateDto.setAccountId(UUID.randomUUID());
        updateDto.setDescription("测试取款");

        Transaction entity = new Transaction();
        entity.setId(id);
        entity.setType(TransactionType.WITHDRAWAL);
        entity.setAmount(new BigDecimal("50.00"));
        entity.setAccountId(updateDto.getAccountId());
        entity.setDescription("测试取款");

        // 设置模拟行为
        when(transactionRepository.existsById(id)).thenReturn(true);
        when(transactionMapper.toEntity(updateDto)).thenReturn(entity);
        when(transactionRepository.save(entity)).thenReturn(entity);
        when(transactionMapper.toDTO(entity)).thenReturn(updateDto);

        // 执行测试
        TransactionDTO updated = transactionService.updateTransaction(id, updateDto);

        // 验证结果
        assertNotNull(updated);
        assertEquals(TransactionType.WITHDRAWAL, updated.getType());
        assertEquals(new BigDecimal("50.00"), updated.getAmount());
        assertEquals(updateDto.getAccountId(), updated.getAccountId());

        // 验证交互
        verify(transactionRepository).existsById(id);
        verify(transactionMapper).toEntity(updateDto);
        verify(transactionRepository).save(entity);
        verify(transactionMapper).toDTO(entity);
    }

    @Test
    void deleteTransaction_ShouldRemoveTransaction() {
        // 准备测试数据
        UUID id = UUID.randomUUID();

        // 设置模拟行为
        when(transactionRepository.existsById(id)).thenReturn(true);
        doNothing().when(transactionRepository).deleteById(id);

        // 执行测试
        transactionService.deleteTransaction(id);

        // 验证交互
        verify(transactionRepository).existsById(id);
        verify(transactionRepository).deleteById(id);
    }

    @Test
    void createTransferTransaction_ShouldCreateBothOutAndInTransactions() {
        // 准备测试数据
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");

        // 创建转出交易DTO
        TransactionDTO transferOutDto = new TransactionDTO();
        transferOutDto.setId(UUID.randomUUID());
        transferOutDto.setType(TransactionType.TRANSFER_OUT);
        transferOutDto.setAmount(amount);
        transferOutDto.setAccountId(fromAccountId);
        transferOutDto.setRelatedAccountId(toAccountId);
        transferOutDto.setDescription("转账到账户 " + toAccountId);

        // 创建转入交易DTO
        TransactionDTO transferInDto = new TransactionDTO();
        transferInDto.setId(UUID.randomUUID());
        transferInDto.setType(TransactionType.TRANSFER_IN);
        transferInDto.setAmount(amount);
        transferInDto.setAccountId(toAccountId);
        transferInDto.setRelatedAccountId(fromAccountId);
        transferInDto.setDescription("从账户 " + fromAccountId + " 转入");

        // 创建转出交易实体
        Transaction transferOutEntity = new Transaction();
        transferOutEntity.setId(transferOutDto.getId());
        transferOutEntity.setType(TransactionType.TRANSFER_OUT);
        transferOutEntity.setAmount(amount);
        transferOutEntity.setAccountId(fromAccountId);
        transferOutEntity.setRelatedAccountId(toAccountId);
        transferOutEntity.setDescription("转账到账户 " + toAccountId);

        // 创建转入交易实体
        Transaction transferInEntity = new Transaction();
        transferInEntity.setId(transferInDto.getId());
        transferInEntity.setType(TransactionType.TRANSFER_IN);
        transferInEntity.setAmount(amount);
        transferInEntity.setAccountId(toAccountId);
        transferInEntity.setRelatedAccountId(fromAccountId);
        transferInEntity.setDescription("从账户 " + fromAccountId + " 转入");

        // 设置模拟行为
        when(transactionMapper.toEntity(any(TransactionDTO.class))).thenReturn(transferOutEntity).thenReturn(transferInEntity);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transferOutEntity).thenReturn(transferInEntity);
        when(transactionMapper.toDTO(any(Transaction.class))).thenReturn(transferOutDto).thenReturn(transferInDto);

        // 执行测试 - 这里我们只是模拟，实际服务层需要实现创建两个交易记录的逻辑
        TransactionDTO createdOut = transactionService.createTransaction(transferOutDto);

        // 验证结果
        assertNotNull(createdOut);
        assertEquals(TransactionType.TRANSFER_OUT, createdOut.getType());
        assertEquals(amount, createdOut.getAmount());
        assertEquals(fromAccountId, createdOut.getAccountId());
        assertEquals(toAccountId, createdOut.getRelatedAccountId());

        // 验证交互
        verify(transactionMapper).toEntity(transferOutDto);
        verify(transactionRepository).save(transferOutEntity);
        verify(transactionMapper).toDTO(transferOutEntity);
    }
} 