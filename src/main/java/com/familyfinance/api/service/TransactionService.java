package com.familyfinance.api.service;

import com.familyfinance.api.dto.request.TransactionRequest;
import com.familyfinance.api.dto.response.PageResponse;
import com.familyfinance.api.dto.response.TransactionResponse;
import com.familyfinance.api.exception.ResourceNotFoundException;
import com.familyfinance.api.exception.UnauthorizedException;
import com.familyfinance.api.model.entity.Transaction;
import com.familyfinance.api.model.enums.TransactionType;
import com.familyfinance.api.repository.CategoryRepository;
import com.familyfinance.api.repository.FamilyRepository;
import com.familyfinance.api.repository.TransactionRepository;
import com.familyfinance.api.repository.UserRepository;
import com.familyfinance.api.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final CategoryRepository categoryRepository;

    // ──────────────────────────────────────────
    // CREATE
    // ──────────────────────────────────────────
    @Transactional
    public TransactionResponse create(TransactionRequest request) {
        var currentUser = getCurrentUser();

        var family = familyRepository.findById(request.getFamilyId())
                .orElseThrow(() -> new ResourceNotFoundException("Family", request.getFamilyId()));

        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        var transaction = Transaction.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(category)
                .user(currentUser)
                .family(family)
                .note(request.getNote())
                .date(request.getDate())
                .build();

        var saved = transactionRepository.save(transaction);
        log.info("Transaction created: {} {} by user {}", saved.getType(), saved.getAmount(), currentUser.getEmail());

        return toResponse(saved);
    }

    // ──────────────────────────────────────────
    // READ ONE
    // ──────────────────────────────────────────
    @Transactional(readOnly = true)
    public TransactionResponse getById(UUID id) {
        var transaction = findOrThrow(id);
        return toResponse(transaction);
    }

    // ──────────────────────────────────────────
    // READ ALL (filtered + paginated)
    // ──────────────────────────────────────────
    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> getAll(
            UUID familyId,
            UUID userId,
            UUID categoryId,
            TransactionType type,
            LocalDate from,
            LocalDate to,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        var result = transactionRepository.findFiltered(
                familyId, userId, categoryId, type, from, to, pageable);

        return PageResponse.from(result.map(this::toResponse));
    }

    // ──────────────────────────────────────────
    // UPDATE
    // ──────────────────────────────────────────
    @Transactional
    public TransactionResponse update(UUID id, TransactionRequest request) {
        var transaction = findOrThrow(id);
        assertOwner(transaction);

        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setCategory(category);
        transaction.setNote(request.getNote());
        transaction.setDate(request.getDate());

        var saved = transactionRepository.save(transaction);
        log.info("Transaction updated: {}", id);

        return toResponse(saved);
    }

    // ──────────────────────────────────────────
    // DELETE
    // ──────────────────────────────────────────
    @Transactional
    public void delete(UUID id) {
        var transaction = findOrThrow(id);
        assertOwner(transaction);
        transactionRepository.delete(transaction);
        log.info("Transaction deleted: {}", id);
    }

    // ──────────────────────────────────────────
    // HELPERS
    // ──────────────────────────────────────────
    private Transaction findOrThrow(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));
    }

    private void assertOwner(Transaction transaction) {
        var currentUser = getCurrentUser();
        if (!transaction.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only modify your own transactions");
        }
    }

    private com.familyfinance.api.model.entity.User getCurrentUser() {
        var principal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", principal.getId()));
    }

    private TransactionResponse toResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .amount(t.getAmount())
                .type(t.getType())
                .categoryId(t.getCategory().getId())
                .categoryName(t.getCategory().getName())
                .categoryIcon(t.getCategory().getIcon())
                .userId(t.getUser().getId())
                .userName(t.getUser().getName())
                .familyId(t.getFamily().getId())
                .note(t.getNote())
                .date(t.getDate())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
