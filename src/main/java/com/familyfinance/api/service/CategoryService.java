package com.familyfinance.api.service;

import com.familyfinance.api.model.entity.Category;
import com.familyfinance.api.model.enums.TransactionType;
import com.familyfinance.api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Category> getByType(TransactionType type) {
        return categoryRepository.findByType(type);
    }
}
