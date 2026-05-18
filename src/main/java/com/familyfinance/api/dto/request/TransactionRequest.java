package com.familyfinance.api.dto.request;

import com.familyfinance.api.model.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class TransactionRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 15, fraction = 4, message = "Invalid amount format")
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @NotNull(message = "Category is required")
    private UUID categoryId;

    @NotNull(message = "Family is required")
    private UUID familyId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Size(max = 500, message = "Note cannot exceed 500 characters")
    private String note;
}
