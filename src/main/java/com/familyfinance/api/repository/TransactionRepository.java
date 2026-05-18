package com.familyfinance.api.repository;

import com.familyfinance.api.model.entity.Transaction;
import com.familyfinance.api.model.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findByFamilyId(UUID familyId, Pageable pageable);

    Page<Transaction> findByFamilyIdAndUserId(UUID familyId, UUID userId, Pageable pageable);

    Page<Transaction> findByFamilyIdAndType(UUID familyId, TransactionType type, Pageable pageable);

    @Query("""
        SELECT t FROM Transaction t
        WHERE t.family.id = :familyId
          AND (:userId IS NULL OR t.user.id = :userId)
          AND (:categoryId IS NULL OR t.category.id = :categoryId)
          AND (:type IS NULL OR t.type = :type)
          AND (:from IS NULL OR t.date >= :from)
          AND (:to IS NULL OR t.date <= :to)
        ORDER BY t.date DESC, t.createdAt DESC
        """)
    Page<Transaction> findFiltered(
            @Param("familyId") UUID familyId,
            @Param("userId") UUID userId,
            @Param("categoryId") UUID categoryId,
            @Param("type") TransactionType type,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            Pageable pageable
    );

    // Sum of expenses for a category in a date range (for budget tracking)
    @Query("""
        SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t
        WHERE t.family.id = :familyId
          AND t.category.id = :categoryId
          AND t.type = 'EXPENSE'
          AND t.date >= :from
          AND t.date <= :to
        """)
    BigDecimal sumExpensesByCategoryAndPeriod(
            @Param("familyId") UUID familyId,
            @Param("categoryId") UUID categoryId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}
