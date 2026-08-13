package com.learning.store.repository;

import com.learning.store.entity.Campaign;
import com.learning.store.model.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Integer> {
    List<Campaign> findByTitleContainingIgnoreCase(String title);

    long countByStatus(CampaignStatus status);

    List<Campaign> findTop5ByOrderByCreatedAtDesc();
}
