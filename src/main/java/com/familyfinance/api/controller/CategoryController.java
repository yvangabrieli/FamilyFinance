package com.familyfinance.api.controller;

import com.familyfinance.api.model.entity.Category;
import com.familyfinance.api.model.enums.TransactionType;
import com.familyfinance.api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // GET /categories               → all categories
    // GET /categories?type=EXPENSE  → filter by type
    @GetMapping
    public ResponseEntity<List<Category>> getAll(
            @RequestParam(required = false) TransactionType type) {
        if (type != null) {
            return ResponseEntity.ok(categoryService.getByType(type));
        }
        return ResponseEntity.ok(categoryService.getAll());
    }
}
