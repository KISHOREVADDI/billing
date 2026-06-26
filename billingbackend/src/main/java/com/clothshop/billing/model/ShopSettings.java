package com.clothshop.billing.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "shop_settings")
public class ShopSettings {
    @Id
    private String id;
    private String shopName;
    private String ownerName;
    private String address;
    private String gstNumber;
    private String mobileNumber;
    private String upiId;
    private String logoBase64;
    private String stampBase64;
    private String qrCodeBase64;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }
    public String getLogoBase64() { return logoBase64; }
    public void setLogoBase64(String logoBase64) { this.logoBase64 = logoBase64; }
    public String getStampBase64() { return stampBase64; }
    public void setStampBase64(String stampBase64) { this.stampBase64 = stampBase64; }
    public String getQrCodeBase64() { return qrCodeBase64; }
    public void setQrCodeBase64(String qrCodeBase64) { this.qrCodeBase64 = qrCodeBase64; }
}
