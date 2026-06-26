package com.clothshop.billing.repository;

import com.clothshop.billing.model.ShopSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface ShopSettingsRepository extends JpaRepository<ShopSettings, Long> {
}
