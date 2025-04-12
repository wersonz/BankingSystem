package com.banking.controller;

import com.banking.dto.TransactionDTO;
import com.banking.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "交易管理", description = "交易管理相关接口")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @Operation(summary = "创建新交易")
    public ResponseEntity<TransactionDTO> createTransaction(@Valid @RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(transactionService.createTransaction(transactionDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取指定交易")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable UUID id) {
        TransactionDTO transaction = transactionService.getTransaction(id);
        return transaction != null ? ResponseEntity.ok(transaction) : ResponseEntity.notFound().build();
    }

    @GetMapping
    @Operation(summary = "获取所有交易")
    public ResponseEntity<List<TransactionDTO>> transactionList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(transactionService.transactionList(page, size));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新交易")
    public ResponseEntity<TransactionDTO> updateTransaction(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, transactionDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除交易")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok().build();
    }
} 