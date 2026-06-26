package com.clothshop.billing.service;

import com.clothshop.billing.model.Bill;
import com.clothshop.billing.model.BillItem;
import com.clothshop.billing.model.ShopSettings;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

@Service
public class PdfGeneratorService {

    @Autowired
    private QrGeneratorService qrGeneratorService;

    public byte[] generateBillPdf(Bill bill, ShopSettings settings) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Logo
            if (settings.getLogoBase64() != null && !settings.getLogoBase64().isEmpty()) {
                try {
                    String base64Image = settings.getLogoBase64().split(",")[1];
                    byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Image);
                    Image logo = Image.getInstance(imageBytes);
                    logo.scaleToFit(100, 100);
                    logo.setAlignment(Element.ALIGN_CENTER);
                    document.add(logo);
                } catch (Exception e) {
                    System.out.println("Failed to add logo: " + e.getMessage());
                }
            }

            // Header
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28);
            Paragraph shopName = new Paragraph(settings.getShopName() != null ? settings.getShopName() : "Shop Name", headerFont);
            shopName.setAlignment(Element.ALIGN_CENTER);
            document.add(shopName);

            Paragraph address = new Paragraph(settings.getAddress() != null ? settings.getAddress() : "");
            address.setAlignment(Element.ALIGN_CENTER);
            document.add(address);

            Paragraph contact = new Paragraph("Phone: " + (settings.getMobileNumber() != null ? settings.getMobileNumber() : "") +
                    " | GST: " + (settings.getGstNumber() != null ? settings.getGstNumber() : ""));
            contact.setAlignment(Element.ALIGN_CENTER);
            document.add(contact);

            document.add(new Paragraph("\n"));
            
            // Divider
            com.itextpdf.text.pdf.draw.LineSeparator ls = new com.itextpdf.text.pdf.draw.LineSeparator();
            document.add(new Chunk(ls));
            document.add(new Paragraph("\n"));

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("TAX INVOICE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Customer Details & Bill Info
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.addCell(getCell("Bill No: " + bill.getBillNumber(), PdfPCell.ALIGN_LEFT));
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            infoTable.addCell(getCell("Date: " + sdf.format(bill.getDate()), PdfPCell.ALIGN_RIGHT));
            infoTable.addCell(getCell("Customer: " + (bill.getCustomerName() != null ? bill.getCustomerName().toUpperCase() : ""), PdfPCell.ALIGN_LEFT));
            infoTable.addCell(getCell("Mobile: " + bill.getMobileNumber(), PdfPCell.ALIGN_RIGHT));
            document.add(infoTable);

            document.add(new Paragraph("\n"));

            // Items Table
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 4, 2, 1, 2, 2});

            table.addCell(getHeaderCell("S.No"));
            table.addCell(getHeaderCell("Item"));
            table.addCell(getHeaderCell("Price Code"));
            table.addCell(getHeaderCell("Qty"));
            table.addCell(getHeaderCell("Rate"));
            table.addCell(getHeaderCell("Amount"));

            int sno = 1;
            for (BillItem item : bill.getItems()) {
                table.addCell(getBorderedCell(String.valueOf(sno++), Element.ALIGN_CENTER));
                table.addCell(getBorderedCell(item.getItemName() != null ? item.getItemName() : "", Element.ALIGN_LEFT));
                table.addCell(getBorderedCell(item.getPriceCode() != null ? item.getPriceCode().toUpperCase() : "", Element.ALIGN_CENTER));
                table.addCell(getBorderedCell(String.valueOf(item.getQuantity()), Element.ALIGN_CENTER));
                table.addCell(getBorderedCell(String.valueOf(item.getRate()), Element.ALIGN_CENTER));
                table.addCell(getBorderedCell(String.valueOf(item.getAmount()), Element.ALIGN_RIGHT));
            }

            document.add(table);
            document.add(new Paragraph("\n"));

            // Totals
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);
            totalTable.addCell(getCell("Subtotal:", PdfPCell.ALIGN_RIGHT));
            totalTable.addCell(getCell(String.valueOf(bill.getSubtotal()), PdfPCell.ALIGN_RIGHT));
            totalTable.addCell(getCell("Discount:", PdfPCell.ALIGN_RIGHT));
            totalTable.addCell(getCell(String.valueOf(bill.getDiscount()), PdfPCell.ALIGN_RIGHT));
            totalTable.addCell(getCell("GST:", PdfPCell.ALIGN_RIGHT));
            totalTable.addCell(getCell(String.valueOf(bill.getGst()), PdfPCell.ALIGN_RIGHT));
            
            PdfPCell grandTotalLabel = getCell("Grand Total:", PdfPCell.ALIGN_RIGHT);
            grandTotalLabel.getFont().setStyle(Font.BOLD);
            totalTable.addCell(grandTotalLabel);
            
            PdfPCell grandTotalValue = getCell(String.valueOf(bill.getGrandTotal()), PdfPCell.ALIGN_RIGHT);
            grandTotalValue.getFont().setStyle(Font.BOLD);
            totalTable.addCell(grandTotalValue);

            document.add(totalTable);
            document.add(new Paragraph("\n\n"));

            // Stamp, QR, and Signature
            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(100);
            
            PdfPCell termsCell = new PdfPCell();
            termsCell.setBorder(PdfPCell.NO_BORDER);
            termsCell.addElement(new Paragraph("Terms & Conditions:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            termsCell.addElement(new Paragraph("1. Goods once sold will not be taken back.", FontFactory.getFont(FontFactory.HELVETICA, 11)));
            termsCell.addElement(new Paragraph("2. Subject to local jurisdiction.", FontFactory.getFont(FontFactory.HELVETICA, 11)));
            footerTable.addCell(termsCell);

            PdfPCell qrCell = new PdfPCell();
            qrCell.setBorder(PdfPCell.NO_BORDER);
            qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            
            String base64Qr = null;
            if (settings.getQrCodeBase64() != null && !settings.getQrCodeBase64().isEmpty()) {
                if (settings.getQrCodeBase64().contains(",")) {
                    base64Qr = settings.getQrCodeBase64().split(",")[1];
                } else {
                    base64Qr = settings.getQrCodeBase64();
                }
            } else if (settings.getUpiId() != null && !settings.getUpiId().isEmpty()) {
                base64Qr = qrGeneratorService.generateUpiQrCodeBase64(settings.getUpiId(), settings.getShopName() != null ? settings.getShopName() : "", bill.getGrandTotal(), bill.getBillNumber());
            }

            if (base64Qr != null) {
                try {
                    byte[] qrBytes = java.util.Base64.getDecoder().decode(base64Qr);
                    Image qrImage = Image.getInstance(qrBytes);
                    qrImage.scaleToFit(80, 80);
                    qrImage.setAlignment(Element.ALIGN_CENTER);
                    qrCell.addElement(qrImage);
                    Paragraph scanText = new Paragraph("Scan to Pay", FontFactory.getFont(FontFactory.HELVETICA, 8));
                    scanText.setAlignment(Element.ALIGN_CENTER);
                    qrCell.addElement(scanText);
                } catch (Exception e) {
                    System.out.println("Failed to add QR: " + e.getMessage());
                }
            }
            footerTable.addCell(qrCell);

            PdfPCell stampCell = new PdfPCell();
            stampCell.setBorder(PdfPCell.NO_BORDER);
            stampCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            
            if (settings.getStampBase64() != null && !settings.getStampBase64().isEmpty()) {
                try {
                    String base64Stamp = settings.getStampBase64().split(",")[1];
                    byte[] stampBytes = java.util.Base64.getDecoder().decode(base64Stamp);
                    Image stamp = Image.getInstance(stampBytes);
                    stamp.scaleToFit(100, 100);
                    stamp.setAlignment(Element.ALIGN_RIGHT);
                    stampCell.addElement(stamp);
                } catch (Exception e) {
                    System.out.println("Failed to add stamp: " + e.getMessage());
                }
            } else {
                Paragraph authSign = new Paragraph("Authorized Signatory", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10));
                authSign.setAlignment(Element.ALIGN_RIGHT);
                stampCell.addElement(new Paragraph("\n\n"));
                stampCell.addElement(authSign);
            }
            footerTable.addCell(stampCell);

            document.add(footerTable);

            document.add(new Paragraph("\n"));
            Paragraph footer = new Paragraph("Thank you for shopping with us! Visit Again.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 14));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    private PdfPCell getCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 14)));
        cell.setPadding(8);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private PdfPCell getHeaderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        return cell;
    }

    private PdfPCell getBorderedCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 14)));
        cell.setPadding(8);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }
}
