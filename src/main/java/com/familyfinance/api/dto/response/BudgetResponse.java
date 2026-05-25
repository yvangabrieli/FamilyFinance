package com.familyfinance.api.dto.response;

import com.familyfinance.api.model.enums.BudgetPeriod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BudgetResponse {

    private UUID id;
    private UUID familyId;

    private UUID categoryId;
    private String categoryName;
    private String categoryIcon;

    private BigDecimal limitAmount;
    private BudgetPeriod period;
    private Integer alertThreshold;

    // Spending summary (calculated at query time)
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private double percentageUsed;
    private boolean isOverBudget;
    private boolean isAlertTriggered;

    private LocalDateTime createdAt;
}
