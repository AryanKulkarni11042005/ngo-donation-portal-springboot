package com.learning.store.controller;

import com.learning.store.dto.CertificateDetailsDto;
import com.learning.store.service.CertificateService;
import com.learning.store.util.CertificatePdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/certificates")
public class CertificateController {

    private final CertificateService certificateService;
    private final String ngoName;

    public CertificateController(CertificateService certificateService,
                                 @Value("${ngo.name:Helping Hands NGO}") String ngoName) {
        this.certificateService = certificateService;
        this.ngoName = ngoName;
    }

    @GetMapping("/{donationId}/download")
    public ResponseEntity<byte[]> download(@PathVariable Integer donationId) {
        CertificateDetailsDto details = certificateService.getOrCreateForDonation(donationId);
        byte[] pdf = CertificatePdfWriter.render(details, ngoName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"certificate-" + details.getCertificateId() + ".pdf\"")
                .body(pdf);
    }

    @GetMapping("/verify/{verificationId}")
    public CertificateDetailsDto verify(@PathVariable String verificationId) {
        return certificateService.getByVerificationId(verificationId);
    }
}
