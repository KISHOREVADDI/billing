package com.clothshop.billing.controller;

import com.clothshop.billing.model.ShopSettings;
import com.clothshop.billing.repository.ShopSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    @Autowired
    private ShopSettingsRepository shopSettingsRepository;

    @GetMapping
    public ResponseEntity<ShopSettings> getSettings() {
        List<ShopSettings> settingsList = shopSettingsRepository.findAll();
        if (settingsList.isEmpty()) {
            return ResponseEntity.ok(new ShopSettings());
        }
        return ResponseEntity.ok(settingsList.get(0));
    }

    @PostMapping
    public ResponseEntity<ShopSettings> saveSettings(@RequestBody ShopSettings settings) {
        List<ShopSettings> settingsList = shopSettingsRepository.findAll();
        if (!settingsList.isEmpty()) {
            settings.setId(settingsList.get(0).getId());
        }
        ShopSettings savedSettings = shopSettingsRepository.save(settings);
        return ResponseEntity.ok(savedSettings);
    }
}
