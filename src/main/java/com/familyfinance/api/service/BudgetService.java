package com.familyfinance.api.service;

import com.familyfinance.api.dto.request.BudgetRequest;
import com.familyfinance.api.dto.response.BudgetResponse;
import com.familyfinance.api.exception.ConflictException;
import com.familyfinance.api.exception.ResourceNotFoundException;
import com.familyfinance.api.model.entity.Budget;
import com.familyfinance.api.model.enums.BudgetPeriod;
import com.familyfinance.api.repository.BudgetRepository;
import com.familyfinance.api.repository.CategoryRepository;
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
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final FamilyRepository familyRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public BudgetResponse create(BudgetRequest request) {
        var family = familyRepository.findById(request.getFamilyId())
                .orElseThrow(() -> new ResourceNotFoundException("Family", request.getFamilyId()));

        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        // One budget per category per family
        if (budgetRepository.findByFamilyIdAndCategoryId(family.getId(), category.getId()).isPresent()) {
            throw new ConflictException(
                    "A budget for '" + category.getName() + "' already exists in this family");
        }

        var budget = Budget.builder()
                .family(family)
                .category(category)
                .limitAmount(request.getLimitAmount())
                .period(request.getPeriod())
                .alertThreshold(request.getAlertThreshold())
                .build();

        var saved = budgetRepository.save(budget);
        log.info("Budget created for category '{}' family '{}'", category.getName(), family.getName());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getByFamily(UUID familyId) {
        return budgetRepository.findByFamilyId(familyId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BudgetResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public BudgetResponse update(UUID id, BudgetRequest request) {
        var budget = findOrThrow(id);

        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        budget.setCategory(category);
        budget.setLimitAmount(request.getLimitAmount());
        budget.setPeriod(request.getPeriod());
        budget.setAlertThreshold(request.getAlertThreshold());

        return toResponse(budgetRepository.save(budget));
    }

    @Transactional
    public void delete(UUID id) {
        budgetRepository.delete(findOrThrow(id));
        log.info("Budget deleted: {}", id);
    }

    // ──────────────────────────────────────────
    // HELPERS
    // ──────────────────────────────────────────
    private Budget findOrThrow(UUID id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", id));
    }

    private BudgetResponse toResponse(Budget b) {
        // Calculate the date range for this budget's period
        LocalDate[] range = getPeriodRange(b.getPeriod());
        LocalDate from = range[0];
        LocalDate to   = range[1];

        BigDecimal spent = transactionRepository.sumExpensesByCategoryAndPeriod(
                b.getFamily().getId(), b.getCategory().getId(), from, to);

        BigDecimal limit     = b.getLimitAmount();
        BigDecimal remaining = limit.subtract(spent);
        double pct = limit.compareTo(BigDecimal.ZERO) == 0 ? 0 :
                spent.divide(limit, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();

        return BudgetResponse.builder()
                .id(b.getId())
                .familyId(b.getFamily().getId())
                .categoryId(b.getCategory().getId())
                .categoryName(b.getCategory().getName())
                .categoryIcon(b.getCategory().getIcon())
                .limitAmount(limit)
                .period(b.getPeriod())
                .alertThreshold(b.getAlertThreshold())
                .spentAmount(spent)
                .remainingAmount(remaining)
                .percentageUsed(Math.round(pct * 10.0) / 10.0)
                .isOverBudget(spent.compareTo(limit) > 0)
                .isAlertTriggered(pct >= b.getAlertThreshold())
                .createdAt(b.getCreatedAt())
                .build();
    }

    private LocalDate[] getPeriodRange(BudgetPeriod period) {
        LocalDate today = LocalDate.now();
        return switch (period) {
            case WEEKLY  -> new LocalDate[]{
                    today.with(java.time.DayOfWeek.MONDAY),
                    today.with(java.time.DayOfWeek.SUNDAY)};
            case MONTHLY -> new LocalDate[]{
                    today.withDayOfMonth(1),
                    today.withDayOfMonth(today.lengthOfMonth())};
            case YEARLY  -> new LocalDate[]{
                    today.withDayOfYear(1),
                    today.withDayOfYear(today.lengthOfYear())};
        };
    }
}
