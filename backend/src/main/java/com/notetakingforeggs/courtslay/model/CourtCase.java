package com.notetakingforeggs.courtslay.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.UUID;

@Entity
@Data
public class CourtCase {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid DEFAULT gen_random_uuid()", nullable = false, updatable = false)
    private UUID id;
    // maybe add @Index here?
    @Column(nullable = false)

    private long startTimeEpoch;
    private Long duration;

    @Column(unique = true)
    private String caseId;

    @CreationTimestamp
    private Long createdAt;

    @Column(columnDefinition = "TEXT")
    private String caseDetails;

    private String claimant;
    private String defendant;
    private Boolean isMinor;

    private String hearingType;
    private String hearingChannel;

    @JsonBackReference
    @ManyToOne(optional = false)
    @JoinColumn(name = "court_id")
    private Court court;
}
