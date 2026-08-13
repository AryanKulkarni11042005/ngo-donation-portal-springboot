package com.learning.store.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="certificates")
@Getter
@Setter
@NoArgsConstructor
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne
    @JoinColumn(name = "donation_id")
    private Donation donation;
    @Column(name = "certificate_code")
    private String certificateCode;
    @Column(name = "verification_id")
    private String verificationId;
    @org.hibernate.annotations.Generated(event = org.hibernate.generator.EventType.INSERT)
    @Column(name = "issued_at", insertable = false, updatable = false)
    private LocalDateTime issuedAt;
}
