package com.notetakingforeggs.courtslay.repository;

import com.notetakingforeggs.courtslay.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Subscription findByChatId(Long id);
}
