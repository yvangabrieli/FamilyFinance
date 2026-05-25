package com.familyfinance.api.service;

import com.familyfinance.api.dto.request.FamilyRequest;
import com.familyfinance.api.dto.response.FamilyResponse;
import com.familyfinance.api.exception.ResourceNotFoundException;
import com.familyfinance.api.model.entity.Family;
import com.familyfinance.api.model.entity.FamilyMember;
import com.familyfinance.api.model.enums.FamilyRole;
import com.familyfinance.api.repository.FamilyMemberRepository;
import com.familyfinance.api.repository.FamilyRepository;
import com.familyfinance.api.repository.UserRepository;
import com.familyfinance.api.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public FamilyResponse create(FamilyRequest request) {
        var currentUser = getCurrentUser();

        var family = Family.builder()
                .name(request.getName())
                .createdBy(currentUser)
                .build();

        var saved = familyRepository.save(family);

        // Creator automatically becomes ADMIN member
        var member = FamilyMember.builder()
                .family(saved)
                .user(currentUser)
                .role(FamilyRole.ADMIN)
                .build();
        familyMemberRepository.save(member);

        log.info("Family '{}' created by {}", saved.getName(), currentUser.getEmail());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public FamilyResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<FamilyResponse> getMyFamilies() {
        var currentUser = getCurrentUser();
        return familyMemberRepository.findByUserId(currentUser.getId())
                .stream()
                .map(fm -> toResponse(fm.getFamily()))
                .toList();
    }

    private Family findOrThrow(UUID id) {
        return familyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Family", id));
    }

    private com.familyfinance.api.model.entity.User getCurrentUser() {
        var principal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", principal.getId()));
    }

    private FamilyResponse toResponse(Family f) {
        return FamilyResponse.builder()
                .id(f.getId())
                .name(f.getName())
                .createdById(f.getCreatedBy().getId())
                .createdByName(f.getCreatedBy().getName())
                .createdAt(f.getCreatedAt())
                .build();
    }
}
