package com.concord.circulationservice.service;

import com.concord.circulationservice.entity.Loan;
import com.concord.circulationservice.entity.LoanStatus;
import com.concord.circulationservice.exception.CopyAlreadyBorrowedException;
import com.concord.circulationservice.repository.LoanRepository;
import org.springframework.stereotype.Service;

@Service
public class LoanService {

    private final LoanRepository loanRepo;

    public LoanService(LoanRepository loanRepo) {
        this.loanRepo = loanRepo;
    }

    public Loan borrowBook(int copyId, String userId) {
        boolean copyIsAvailable = !(loanRepo.existsByCopyIdAndLoanStatus(copyId, LoanStatus.ACTIVE));
        if (copyIsAvailable) {
            // REST call to Catalog Service to get the copy
            // build the loan row and loanRepo.save(loan)
            // PATCH call to Catalog Service to change status to "BORROWED"
        } else {
            throw new CopyAlreadyBorrowedException("Book is already borrowed");
        }
        return null; // temporary, until the borrow logic is filled in
    }
}