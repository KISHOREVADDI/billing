package com.clothshop.billing.repository;

import com.clothshop.billing.model.ShopSettings;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface ShopSettingsRepository extends MongoRepository<ShopSettings, String> {
}
