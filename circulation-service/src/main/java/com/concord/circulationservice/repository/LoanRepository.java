package com.concord.circulationservice.repository;

import com.concord.circulationservice.entity.Loan;
import com.concord.circulationservice.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Integer> {

public boolean existsByCopyIdAndLoanStatus(int copyId, LoanStatus loanStatus);

}