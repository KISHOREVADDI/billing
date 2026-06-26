package com.clothshop.billing.model;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "shop_settings")
public class ShopSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String shopName;
    private String ownerName;
    private String address;
    private String gstNumber;
    private String mobileNumber;
    private String upiId;
    private String logoBase64;
    private String stampBase64;
}
