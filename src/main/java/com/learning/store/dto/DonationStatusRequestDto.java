package com.learning.store.dto;

import com.learning.store.model.DonationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DonationStatusRequestDto {
    @NotNull(message = "Status is required")
    private DonationStatus status;
}
