package com.familyfinance.api.service;

import com.familyfinance.api.dto.response.InsightResponse;
import com.familyfinance.api.dto.response.InsightResponse.CategoryBreakdown;
import com.familyfinance.api.exception.ResourceNotFoundException;
import com.familyfinance.api.repository.BudgetRepository;
import com.familyfinance.api.repository.FamilyRepository;
import com.familyfinance.api.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsightService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final FamilyRepository familyRepository;

    @Transactional(readOnly = true)
    public InsightResponse getMonthlySummary(UUID familyId, int year, int month) {
        familyRepository.findById(familyId)
                .orElseThrow(() -> new ResourceNotFoundException("Family", familyId));

        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate to   = from.withDayOfMonth(from.lengthOfMonth());

        BigDecimal totalIncome   = transactionRepository.sumIncomeByPeriod(familyId, from, to);
        BigDecimal totalExpenses = transactionRepository.sumExpensesByPeriod(familyId, from, to);
        BigDecimal netBalance    = totalIncome.subtract(totalExpenses);

        // Category breakdown for top expenses
        List<Object[]> rows = transactionRepository.sumExpensesGroupedByCategory(familyId, from, to);
        List<CategoryBreakdown> breakdown = rows.stream()
                .limit(5)
                .map(row -> {
                    String name  = (String) row[0];
                    String icon  = (String) row[1];
                    BigDecimal amt = (BigDecimal) row[2];
                    double pct = totalExpenses.compareTo(BigDecimal.ZERO) == 0 ? 0 :
                            amt.divide(totalExpenses, 4, RoundingMode.HALF_UP)
                               .multiply(BigDecimal.valueOf(100))
                               .doubleValue();
                    return CategoryBreakdown.builder()
                            .categoryName(name)
                            .categoryIcon(icon)
                            .amount(amt)
                            .percentage(Math.round(pct * 10.0) / 10.0)
                            .build();
                })
                .toList();

        int score = calculateSatisfactionScore(familyId, from, to);

        return InsightResponse.builder()
                .year(year)
                .month(month)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .satisfactionScore(score)
                .topExpenseCategories(breakdown)
                .build();
    }

    // ──────────────────────────────────────────────────────────────────
    // Satisfaction score algorithm (0–100)
    //
    // The score answers: "How well did the family respect their budgets?"
    //
    // Logic:
    //   - For each budget: calculate % of limit used (spent / limit * 100)
    //   - Under 100% = good. The closer to 0% the better.
    //   - Over 100%  = penalty proportional to overspend
    //   - No budgets set = neutral score of 70
    //   - Average score across all budgets → clamp to [0, 100]
    // ──────────────────────────────────────────────────────────────────
    private int calculateSatisfactionScore(UUID familyId, LocalDate from, LocalDate to) {
        var budgets = budgetRepository.findByFamilyId(familyId);

        if (budgets.isEmpty()) {
            return 70; // No budgets = neutral
        }

        double totalScore = 0;
        for (var budget : budgets) {
            BigDecimal spent = transactionRepository.sumExpensesByCategoryAndPeriod(
                    familyId, budget.getCategory().getId(), from, to);
            BigDecimal limit = budget.getLimitAmount();

            if (limit.compareTo(BigDecimal.ZERO) == 0) continue;

            double pct = spent.divide(limit, 4, RoundingMode.HALF_UP).doubleValue();

            double budgetScore;
            if (pct <= 0.5) {
                budgetScore = 100; // Under 50% of budget — excellent
            } else if (pct <= 1.0) {
                // Between 50% and 100% — score scales from 100 down to 60
                budgetScore = 100 - ((pct - 0.5) / 0.5) * 40;
            } else {
                // Over budget — heavy penalty
                budgetScore = Math.max(0, 60 - ((pct - 1.0) * 100));
            }

            totalScore += budgetScore;
        }

        int score = (int) Math.round(totalScore / budgets.size());
        return Math.max(0, Math.min(100, score));
    }
}
