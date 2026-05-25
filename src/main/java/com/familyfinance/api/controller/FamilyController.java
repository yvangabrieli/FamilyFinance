package com.familyfinance.api.controller;

import com.familyfinance.api.dto.request.FamilyRequest;
import com.familyfinance.api.dto.response.FamilyResponse;
import com.familyfinance.api.service.FamilyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/families")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyService familyService;

    // POST /families — create a new family group
    @PostMapping
    public ResponseEntity<FamilyResponse> create(@Valid @RequestBody FamilyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(familyService.create(request));
    }

    // GET /families/{id} — get one family
    @GetMapping("/{id}")
    public ResponseEntity<FamilyResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(familyService.getById(id));
    }

    // GET /families/mine — all families the current user belongs to
    @GetMapping("/mine")
    public ResponseEntity<List<FamilyResponse>> getMyFamilies() {
        return ResponseEntity.ok(familyService.getMyFamilies());
    }
}
