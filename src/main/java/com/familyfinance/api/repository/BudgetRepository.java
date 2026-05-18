package com.familyfinance.api.repository;

import com.familyfinance.api.model.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    List<Budget> findByFamilyId(UUID familyId);

    Optional<Budget> findByFamilyIdAndCategoryId(UUID familyId, UUID categoryId);
}
