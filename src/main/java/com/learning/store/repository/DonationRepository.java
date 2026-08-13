package com.learning.store.repository;

import com.learning.store.entity.Donation;
import com.learning.store.model.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Integer> {

    long countByStatus(DonationStatus status);

    long countByCampaignId(Integer campaignId);

    List<Donation> findTop5ByOrderByCreatedAtDesc();

    @Query("select count(distinct d.donorEmail) from Donation d")
    long countDistinctDonorEmail();

    // cast(:search as string) keeps Postgres from guessing bytea for a null parameter.
    @Query("""
            select d from Donation d
            where (:status is null or d.status = :status)
              and (cast(:search as string) is null
                   or lower(d.donorName) like lower(concat('%', cast(:search as string), '%'))
                   or lower(d.donorEmail) like lower(concat('%', cast(:search as string), '%'))
                   or lower(d.transactionId) like lower(concat('%', cast(:search as string), '%')))
            order by d.createdAt desc
            """)
    List<Donation> search(@Param("search") String search, @Param("status") DonationStatus status);
}
