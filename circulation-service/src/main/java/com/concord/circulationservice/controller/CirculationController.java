package com.concord.circulationservice.controller;

import com.concord.circulationservice.entity.Loan;
import com.concord.circulationservice.service.LoanService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class CirculationController {

    private final LoanService loanService;

    public CirculationController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/borrow")
    public Loan borrowBook(@RequestParam int copyId, @RequestParam String userId) {
        return loanService.borrowBook(copyId, userId);
    }
}
