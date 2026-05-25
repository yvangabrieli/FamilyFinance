package com.familyfinance.api.controller;

import com.familyfinance.api.dto.request.TransactionRequest;
import com.familyfinance.api.dto.response.PageResponse;
import com.familyfinance.api.dto.response.TransactionResponse;
import com.familyfinance.api.model.enums.TransactionType;
import com.familyfinance.api.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // ── POST /transactions ──────────────────────────────
    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.create(request));
    }

    // ── GET /transactions/{id} ──────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.getById(id));
    }

    // ── GET /transactions?familyId=...&type=EXPENSE&from=2024-01-01 ────
    @GetMapping
    public ResponseEntity<PageResponse<TransactionResponse>> getAll(
            @RequestParam UUID familyId,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                transactionService.getAll(familyId, userId, categoryId, type, from, to, page, size));
    }

    // ── PUT /transactions/{id} ──────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.update(id, request));
    }

    // ── DELETE /transactions/{id} ───────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
