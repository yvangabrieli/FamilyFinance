package com.familyfinance.api.repository;

import com.familyfinance.api.model.entity.Category;
import com.familyfinance.api.model.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByType(TransactionType type);

    List<Category> findByIsDefaultTrue();
}
