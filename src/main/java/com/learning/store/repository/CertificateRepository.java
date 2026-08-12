package com.learning.store.repository;

import com.learning.store.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {
}
