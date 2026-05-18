package com.familyfinance.api.repository;

import com.familyfinance.api.model.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, UUID> {

    List<FamilyMember> findByFamilyId(UUID familyId);

    Optional<FamilyMember> findByFamilyIdAndUserId(UUID familyId, UUID userId);

    boolean existsByFamilyIdAndUserId(UUID familyId, UUID userId);

    List<FamilyMember> findByUserId(UUID userId);
}
