package com.learning.store.entity;

import com.learning.store.model.CampaignStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.generator.EventType;

import java.math.BigDecimal;
import java.time.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "campaigns")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String description;
    @Column(name="target_amount")
    private BigDecimal targetAmount;
    @Column(name="current_amount")
    private BigDecimal currentAmount;
    private CampaignStatus status;
    @Column(name="start_date")
    private LocalDate startDate;
    @Column(name="end_date")
    private LocalDate endDate;
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;

}
