package com.concord.circulationservice.repository;

import com.concord.circulationservice.entity.Loan;
import com.concord.circulationservice.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Integer> {

public boolean existsByCopyIdAndLoanStatus(int copyId, LoanStatus loanStatus);

Optional<Loan> findByCopyIdAndLoanStatus(int copyId, LoanStatus loanStatus);

}