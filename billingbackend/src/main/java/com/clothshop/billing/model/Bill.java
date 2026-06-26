package com.clothshop.billing.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Document(collection = "bills")
public class Bill {
    @Id
    private String id;
    private String billNumber;
    private Date date;
    private String customerName;
    private String mobileNumber;
    
    private List<BillItem> items;
    private double subtotal;
    private double discountPercent;
    private double discount;
    private double gstPercent;
    private double gst;
    private double grandTotal;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public List<BillItem> getItems() { return items; }
    public void setItems(List<BillItem> items) { this.items = items; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }
    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }
    public double getGstPercent() { return gstPercent; }
    public void setGstPercent(double gstPercent) { this.gstPercent = gstPercent; }
    public double getGst() { return gst; }
    public void setGst(double gst) { this.gst = gst; }
    public double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(double grandTotal) { this.grandTotal = grandTotal; }
}
