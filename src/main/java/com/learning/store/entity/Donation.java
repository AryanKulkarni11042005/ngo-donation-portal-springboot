package com.learning.store.entity;

import com.learning.store.model.DonationStatus;
import com.learning.store.model.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="donations")
@Getter
@Setter
@NoArgsConstructor
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name="campaign_id")
    private Campaign campaign;
    @Column(name="donor_name")
    private String donorName;
    @Column(name="donor_email")
    private String donorEmail;
    @Column(name="donor_phone")
    private String donorPhone;
    private BigDecimal amount;
    @Column(name="transaction_id")
    private String transactionId;
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;
    private DonationStatus status;
    // Populated by the column default; read back so the response carries it.
    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
