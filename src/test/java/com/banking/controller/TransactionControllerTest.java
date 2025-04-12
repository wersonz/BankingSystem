package com.banking.controller;

import com.banking.dto.TransactionDTO;
import com.banking.enums.TransactionType;
import com.banking.exception.TransactionNotFoundException;
import com.banking.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionDTO validTransactionDTO;
    private UUID transactionId;

    @BeforeEach
    void setUp() {
        transactionId = UUID.randomUUID();
        validTransactionDTO = new TransactionDTO();
        validTransactionDTO.setId(transactionId);
        validTransactionDTO.setType(TransactionType.DEPOSIT);
        validTransactionDTO.setAmount(new BigDecimal("100.0"));
        validTransactionDTO.setAccountId(UUID.randomUUID());
        validTransactionDTO.setDescription("测试存款");
        validTransactionDTO.setCreatedAt(LocalDateTime.now());
        validTransactionDTO.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createTransaction_ShouldReturnCreatedTransaction() throws Exception {
        when(transactionService.createTransaction(any(TransactionDTO.class)))
                .thenReturn(validTransactionDTO);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTransactionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId.toString()))
                .andExpect(jsonPath("$.type").value(TransactionType.DEPOSIT.name()))
                .andExpect(jsonPath("$.amount").value("100.0"))
                .andExpect(jsonPath("$.description").value("测试存款"));

        verify(transactionService).createTransaction(any(TransactionDTO.class));
    }

    @Test
    void createTransaction_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        TransactionDTO invalidDTO = new TransactionDTO();
        // 缺少必要字段

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).createTransaction(any(TransactionDTO.class));
    }

    @Test
    void getTransaction_ShouldReturnTransaction_WhenExists() throws Exception {
        when(transactionService.getTransaction(transactionId))
                .thenReturn(validTransactionDTO);

        mockMvc.perform(get("/api/transactions/{id}", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId.toString()))
                .andExpect(jsonPath("$.type").value(TransactionType.DEPOSIT.name()))
                .andExpect(jsonPath("$.amount").value("100.0"));

        verify(transactionService).getTransaction(transactionId);
    }

    @Test
    void getTransaction_ShouldReturnNotFound_WhenNotExists() throws Exception {
        when(transactionService.getTransaction(transactionId))
                .thenThrow(new TransactionNotFoundException(transactionId));

        mockMvc.perform(get("/api/transactions/{id}", transactionId))
                .andExpect(status().isNotFound());

        verify(transactionService).getTransaction(transactionId);
    }

    @Test
    void transactionList_ShouldReturnPaginatedResults() throws Exception {
        List<TransactionDTO> transactions = Arrays.asList(validTransactionDTO);
        when(transactionService.transactionList(eq(0), eq(10)))
                .thenReturn(transactions);

        mockMvc.perform(get("/api/transactions")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(transactionId.toString()))
                .andExpect(jsonPath("$[0].type").value(TransactionType.DEPOSIT.name()))
                .andExpect(jsonPath("$[0].amount").value("100.0"));

        verify(transactionService).transactionList(0, 10);
    }

    @Test
    void updateTransaction_ShouldUpdateExistingTransaction() throws Exception {
        when(transactionService.updateTransaction(eq(transactionId), any(TransactionDTO.class)))
                .thenReturn(validTransactionDTO);

        mockMvc.perform(put("/api/transactions/{id}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTransactionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId.toString()))
                .andExpect(jsonPath("$.type").value(TransactionType.DEPOSIT.name()))
                .andExpect(jsonPath("$.amount").value("100.0"));

        verify(transactionService).updateTransaction(eq(transactionId), any(TransactionDTO.class));
    }

    @Test
    void updateTransaction_ShouldReturnNotFound_WhenNotExists() throws Exception {
        when(transactionService.updateTransaction(eq(transactionId), any(TransactionDTO.class)))
                .thenThrow(new TransactionNotFoundException(transactionId));

        mockMvc.perform(put("/api/transactions/{id}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTransactionDTO)))
                .andExpect(status().isNotFound());

        verify(transactionService).updateTransaction(eq(transactionId), any(TransactionDTO.class));
    }

    @Test
    void deleteTransaction_ShouldDeleteExistingTransaction() throws Exception {
        doNothing().when(transactionService).deleteTransaction(transactionId);

        mockMvc.perform(delete("/api/transactions/{id}", transactionId))
                .andExpect(status().isOk());

        verify(transactionService).deleteTransaction(transactionId);
    }

    @Test
    void deleteTransaction_ShouldReturnNotFound_WhenNotExists() throws Exception {
        doThrow(new TransactionNotFoundException(transactionId))
                .when(transactionService).deleteTransaction(transactionId);

        mockMvc.perform(delete("/api/transactions/{id}", transactionId))
                .andExpect(status().isNotFound());

        verify(transactionService).deleteTransaction(transactionId);
    }
} 