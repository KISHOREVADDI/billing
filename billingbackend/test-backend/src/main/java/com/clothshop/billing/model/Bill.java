package com.clothshop.billing.model;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "bills")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String billNumber;
    private Date date;
    private String customerName;
    private String mobileNumber;
    
    @ElementCollection
    @CollectionTable(name = "bill_items", joinColumns = @JoinColumn(name = "bill_id"))
    private List<BillItem> items;
    private double subtotal;
    private double discount;
    private double gst;
    private double grandTotal;
}
