package com.learning.store.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.learning.store.dto.CertificateDetailsDto;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Renders the donation certificate, matching the layout the Node backend produced.
 */
public final class CertificatePdfWriter {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("EEE MMM dd yyyy");

    private CertificatePdfWriter() {
    }

    public static byte[] render(CertificateDetailsDto details, String ngoName) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("NGO Donation Certificate", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(30);
            document.add(title);

            Font body = FontFactory.getFont(FontFactory.HELVETICA, 12);
            document.add(new Paragraph("Certificate ID: " + details.getCertificateId(), body));
            document.add(new Paragraph("Verification ID: " + details.getVerificationId(), body));
            document.add(new Paragraph(" ", body));
            document.add(new Paragraph("Donor Name: " + details.getDonorName(), body));
            document.add(new Paragraph("Campaign: " + details.getCampaignTitle(), body));
            document.add(new Paragraph("Donation Amount: " + details.getAmount(), body));
            document.add(new Paragraph("Transaction ID: " + details.getTransactionId(), body));
            document.add(new Paragraph(
                    "Date: " + (details.getDate() == null ? "" : details.getDate().format(DATE)), body));
            document.add(new Paragraph(" ", body));
            document.add(new Paragraph("NGO Name: " + ngoName, body));

            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Paragraph footer = new Paragraph("Thank you for supporting our mission.", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(45);
            document.add(footer);

            document.close();
        } catch (DocumentException ex) {
            throw new IllegalStateException("Failed to generate certificate PDF", ex);
        }

        return out.toByteArray();
    }
}
