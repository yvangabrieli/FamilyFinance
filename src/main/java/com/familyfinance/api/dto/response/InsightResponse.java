package com.familyfinance.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class InsightResponse {

    private int year;
    private int month;

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;

    // 0-100 score: how well the family stayed within budgets this month
    private int satisfactionScore;

    private List<CategoryBreakdown> topExpenseCategories;

    @Data
    @Builder
    public static class CategoryBreakdown {
        private String categoryName;
        private String categoryIcon;
        private BigDecimal amount;
        private double percentage; // % of total expenses
    }
}
