package com.familyfinance.api.dto.request;

import com.familyfinance.api.model.enums.BudgetPeriod;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BudgetRequest {

    @NotNull(message = "Family is required")
    private UUID familyId;

    @NotNull(message = "Category is required")
    private UUID categoryId;

    @NotNull(message = "Limit amount is required")
    @DecimalMin(value = "0.01", message = "Limit must be greater than 0")
    @Digits(integer = 15, fraction = 4, message = "Invalid amount format")
    private BigDecimal limitAmount;

    @NotNull(message = "Period is required")
    private BudgetPeriod period;

    @Min(value = 1, message = "Threshold must be between 1 and 100")
    @Max(value = 100, message = "Threshold must be between 1 and 100")
    private Integer alertThreshold = 80;
}
