package com.clothshop.billing.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class BillItem {
    private String itemName;
    private int quantity;
    private double rate;
    private double amount;
}
