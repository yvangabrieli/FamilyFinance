package com.familyfinance.api.dto.response;

import com.familyfinance.api.model.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TransactionResponse {

    private UUID id;
    private BigDecimal amount;
    private TransactionType type;

    // Flattened category info — mobile doesn't need the full Category object
    private UUID categoryId;
    private String categoryName;
    private String categoryIcon;

    // Who made it
    private UUID userId;
    private String userName;

    private UUID familyId;
    private String note;
    private LocalDate date;
    private LocalDateTime createdAt;
}
