package com.clothshop.billing.repository;

import com.clothshop.billing.model.Bill;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends MongoRepository<Bill, String> {
    List<Bill> findByDateBetween(Date startDate, Date endDate);
    Bill findTopByOrderByBillNumberDesc();
}
