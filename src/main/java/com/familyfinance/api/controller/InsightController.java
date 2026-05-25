package com.familyfinance.api.controller;

import com.familyfinance.api.dto.response.InsightResponse;
import com.familyfinance.api.service.InsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/insights")
@RequiredArgsConstructor
public class InsightController {

    private final InsightService insightService;

    // GET /insights/monthly?familyId=...&year=2026&month=5
    // Defaults to current month if year/month not provided
    @GetMapping("/monthly")
    public ResponseEntity<InsightResponse> getMonthlySummary(
            @RequestParam UUID familyId,
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int month) {

        // Default to current month
        if (year == 0) year = LocalDate.now().getYear();
        if (month == 0) month = LocalDate.now().getMonthValue();

        return ResponseEntity.ok(insightService.getMonthlySummary(familyId, year, month));
    }
}
