package com.clothshop.billing.controller;

import com.clothshop.billing.model.Bill;
import com.clothshop.billing.model.ShopSettings;
import com.clothshop.billing.repository.BillRepository;
import com.clothshop.billing.repository.ShopSettingsRepository;
import com.clothshop.billing.service.PdfGeneratorService;
import com.clothshop.billing.service.QrGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private ShopSettingsRepository shopSettingsRepository;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Autowired
    private QrGeneratorService qrGeneratorService;

    @PostMapping
    public ResponseEntity<Bill> createBill(@RequestBody Bill bill) {
        if (bill.getBillNumber() == null || bill.getBillNumber().isEmpty()) {
            Bill lastBill = billRepository.findTopByOrderByBillNumberDesc();
            int nextNumber = 1;
            if (lastBill != null && lastBill.getBillNumber() != null) {
                try {
                    nextNumber = Integer.parseInt(lastBill.getBillNumber().replace("INV-", "")) + 1;
                } catch (Exception e) {}
            }
            bill.setBillNumber(String.format("INV-%05d", nextNumber));
        }
        if (bill.getDate() == null) {
            bill.setDate(new Date());
        }
        Bill savedBill = billRepository.save(bill);
        return ResponseEntity.ok(savedBill);
    }

    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills() {
        return ResponseEntity.ok(billRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bill> getBillById(@PathVariable String id) {
        return billRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateBillPdf(@PathVariable String id) {
        Bill bill = billRepository.findById(id).orElse(null);
        if (bill == null) {
            return ResponseEntity.notFound().build();
        }

        List<ShopSettings> settingsList = shopSettingsRepository.findAll();
        ShopSettings settings = settingsList.isEmpty() ? new ShopSettings() : settingsList.get(0);

        byte[] pdf = pdfGeneratorService.generateBillPdf(bill, settings);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "bill_" + bill.getBillNumber() + ".pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @GetMapping("/qr")
    public ResponseEntity<Map<String, String>> generateQrCode(@RequestParam double amount, @RequestParam(required = false) String billNumber) {
        List<ShopSettings> settingsList = shopSettingsRepository.findAll();
        if (settingsList.isEmpty() || settingsList.get(0).getUpiId() == null) {
            return ResponseEntity.badRequest().build();
        }
        ShopSettings settings = settingsList.get(0);
        String qrBase64 = qrGeneratorService.generateUpiQrCodeBase64(settings.getUpiId(), settings.getShopName(), amount, billNumber);
        
        Map<String, String> response = new HashMap<>();
        response.put("qrCode", qrBase64);
        response.put("upiId", settings.getUpiId());
        
        return ResponseEntity.ok(response);
    }
}
