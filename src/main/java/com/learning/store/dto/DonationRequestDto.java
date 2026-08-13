package com.learning.store.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class DonationRequestDto {

    @NotNull(message = "Campaign id is required")
    private Integer campaignId;

    @NotBlank(message = "Donor name is required")
    @Size(max = 100, message = "Donor name must be at most 100 characters")
    private String donorName;

    @NotBlank(message = "Donor email is required")
    @Email(message = "Donor email must be valid")
    private String donorEmail;

    @NotBlank(message = "Donor phone is required")
    @Size(max = 20, message = "Donor phone must be at most 20 characters")
    private String donorPhone;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    // Optional: generated server-side when the client does not supply one.
    private String transactionId;
}
