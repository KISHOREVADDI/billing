package com.clothshop.billing.repository;

import com.clothshop.billing.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByDateBetween(Date startDate, Date endDate);
    Bill findTopByOrderByBillNumberDesc();
}
