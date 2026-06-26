package com.clothshop.billing.service;

import com.clothshop.billing.model.Bill;
import com.clothshop.billing.model.BillItem;
import com.clothshop.billing.model.ShopSettings;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

@Service
public class PdfGeneratorService {

    public byte[] generateBillPdf(Bill bill, ShopSettings settings) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Header
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
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

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Paragraph title = new Paragraph("INVOICE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Customer Details & Bill Info
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.addCell(getCell("Bill No: " + bill.getBillNumber(), PdfPCell.ALIGN_LEFT));
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            infoTable.addCell(getCell("Date: " + sdf.format(bill.getDate()), PdfPCell.ALIGN_RIGHT));
            infoTable.addCell(getCell("Customer: " + bill.getCustomerName(), PdfPCell.ALIGN_LEFT));
            infoTable.addCell(getCell("Mobile: " + bill.getMobileNumber(), PdfPCell.ALIGN_RIGHT));
            document.add(infoTable);

            document.add(new Paragraph("\n"));

            // Items Table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 4, 1, 2, 2});

            table.addCell(getHeaderCell("S.No"));
            table.addCell(getHeaderCell("Item"));
            table.addCell(getHeaderCell("Qty"));
            table.addCell(getHeaderCell("Rate"));
            table.addCell(getHeaderCell("Amount"));

            int sno = 1;
            for (BillItem item : bill.getItems()) {
                table.addCell(String.valueOf(sno++));
                table.addCell(item.getItemName());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(String.valueOf(item.getRate()));
                table.addCell(String.valueOf(item.getAmount()));
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

            document.add(new Paragraph("\n"));
            Paragraph footer = new Paragraph("Thank you for shopping with us!");
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    private PdfPCell getCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private PdfPCell getHeaderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }
}
