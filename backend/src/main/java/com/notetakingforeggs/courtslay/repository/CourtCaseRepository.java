package com.notetakingforeggs.courtslay.repository;

import com.notetakingforeggs.courtslay.model.CourtCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface CourtCaseRepository extends JpaRepository <CourtCase, Long> {
List<CourtCase> findByCourt_Region_Id(long regionId);
List<CourtCase> findByCourt_Id(long courtId);
List<CourtCase> findByClaimantContainingIgnoreCase(String claimant);
List<CourtCase> findByClaimantContainingIgnoreCaseAndCreatedAtAfter(String claimant, Long createdAt);
List<CourtCase> findByDefendantContainingIgnoreCase(String defendant);
List<CourtCase> findByDefendantContainingIgnoreCaseAndCreatedAtAfter(String defendant, Long createdAt);
List<CourtCase> findByHearingTypeContainingIgnoreCase(String hearingType);
}
