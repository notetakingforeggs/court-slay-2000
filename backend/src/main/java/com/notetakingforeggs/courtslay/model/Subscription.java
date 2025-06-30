package com.notetakingforeggs.courtslay.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
@Entity
public class Subscription {
    @Id
    @GeneratedValue
    private Long id;

    private Long chatId;

    private List<String> alertTermsClaimant = new ArrayList<>();
    private List<String> alertTermsDefendant = new ArrayList<>();

    private Long lastNotifiedTimestamp;
}
