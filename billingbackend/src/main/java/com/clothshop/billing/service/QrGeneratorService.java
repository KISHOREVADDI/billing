package com.clothshop.billing.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class QrGeneratorService {

    public String generateUpiQrCodeBase64(String upiId, String shopName, double amount, String billNumber) {
        try {
            String upiUrl = String.format("upi://pay?pa=%s&pn=%s&am=%.2f&cu=INR&tn=Bill_%s", upiId, shopName, amount, billNumber != null ? billNumber : "");
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(upiUrl, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(pngData);
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
