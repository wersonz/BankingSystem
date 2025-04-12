package com.banking.service.impl;

import com.banking.dto.TransactionDTO;
import com.banking.entity.Transaction;
import com.banking.exception.DuplicateTransactionException;
import com.banking.exception.InvalidTransactionException;
import com.banking.exception.TransactionNotFoundException;
import com.banking.mapper.TransactionMapper;
import com.banking.repository.TransactionRepository;
import com.banking.service.TransactionService;
import com.google.common.util.concurrent.Striped;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    private final Striped<Lock> stripedLocks = Striped.lock(2048);

    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    @CacheEvict(value = "transactionList", allEntries = true)
    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        Lock lock = stripedLocks.get(transactionDTO.getId());
        lock.lock();
        try {
            // 检查是否存在重复交易
            if (transactionRepository.existsById(transactionDTO.getId())) {
                throw new DuplicateTransactionException("交易ID " + transactionDTO.getId() + " 已存在");
            }

            Transaction entity = transactionMapper.toEntity(transactionDTO);
            Transaction savedEntity = transactionRepository.save(entity);
            return transactionMapper.toDTO(savedEntity);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Cacheable(value = "transactions", key = "#id.toString()")
    public TransactionDTO getTransaction(UUID id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toDTO)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    @Override
    @Cacheable(value = "transactionList", key = "'page:' + #page + ':size:' + #size")
    public List<TransactionDTO> transactionList(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new InvalidTransactionException("页码必须大于等于0，每页大小必须大于0");
        }

        return transactionRepository.findAll(PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "transactions", key = "#id.toString()"),
            @CacheEvict(value = "transactionList", allEntries = true)
    })
    public TransactionDTO updateTransaction(UUID id, TransactionDTO transactionDTO) {
        Lock lock = stripedLocks.get(id);
        lock.lock();
        try {
            // 检查交易是否存在
            if (!transactionRepository.existsById(id)) {
                throw new TransactionNotFoundException(id);
            }

            Transaction entity = transactionMapper.toEntity(transactionDTO);
            Transaction updatedEntity = transactionRepository.save(entity);
            return transactionMapper.toDTO(updatedEntity);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "transactions", key = "#id.toString()"),
            @CacheEvict(value = "transactionList", allEntries = true)
    })
    public void deleteTransaction(UUID id) {
        Lock lock = stripedLocks.get(id);
        lock.lock();
        try {
            if (!transactionRepository.existsById(id)) {
                throw new TransactionNotFoundException(id);
            }
            transactionRepository.deleteById(id);
        } finally {
            lock.unlock();
        }
    }
} 