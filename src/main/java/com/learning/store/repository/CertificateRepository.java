package com.learning.store.repository;

import com.learning.store.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {
    Optional<Certificate> findByDonationId(Integer donationId);

    Optional<Certificate> findByVerificationId(String verificationId);
}
