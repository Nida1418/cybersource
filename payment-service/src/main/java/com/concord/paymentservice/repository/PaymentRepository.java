package com.concord.paymentservice.repository;

import com.concord.paymentservice.entity.Payment;
import com.concord.paymentservice.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByLoanId(int loanId);

    Optional<Payment> findByLoanId(int loanId);
}
