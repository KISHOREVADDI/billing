package com.clothshop.billing.model;

public class BillItem {
    private String itemName;
    private String priceCode;
    private String unit;
    private int quantity;
    private double rate;
    private double amount;

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getPriceCode() { return priceCode; }
    public void setPriceCode(String priceCode) { this.priceCode = priceCode; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
