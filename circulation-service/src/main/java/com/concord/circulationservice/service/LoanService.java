package com.concord.circulationservice.service;

import com.concord.circulationservice.dto.CopyResponse;
import com.concord.circulationservice.entity.Loan;
import com.concord.circulationservice.entity.LoanStatus;
import com.concord.circulationservice.entity.OutboxEvent;
import com.concord.circulationservice.entity.OutboxStatus;
import com.concord.circulationservice.event.FineGeneratedEvent;
import com.concord.circulationservice.exception.CopyAlreadyBorrowedException;
import com.concord.circulationservice.repository.LoanRepository;
import com.concord.circulationservice.repository.OutboxEventRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

@Service
public class LoanService {

    private final LoanRepository loanRepo;
    private final CatalogClient catalogClient;
    private final OutboxEventRepository outboxRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoanService(LoanRepository loanRepo, CatalogClient catalogClient, OutboxEventRepository outboxRepo) {
        this.loanRepo = loanRepo;
        this.catalogClient = catalogClient;
        this.outboxRepo = outboxRepo;
    }

    public Loan borrowBook(int copyId, String userId) {
        boolean copyIsAvailable = !(loanRepo.existsByCopyIdAndLoanStatus(copyId, LoanStatus.ACTIVE));
        if (!copyIsAvailable) {
            throw new CopyAlreadyBorrowedException("Book is already borrowed");
        }

        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        String token = jwt.getTokenValue();

        CopyResponse copy = catalogClient.getCopyFromCatalogService(copyId, token);

        if ("BORROWED".equals(copy.getStatus())) {
            throw new CopyAlreadyBorrowedException("Book is already borrowed");
        } else if ("AVAILABLE".equals(copy.getStatus())) {
            Loan loan = new Loan();
            loan.setCopyId(copyId);
            loan.setUserId(userId);
            loan.setLoanStatus(LoanStatus.ACTIVE);
            loan.setLoanDate(LocalDate.now());
            loan.setDueDate(LocalDate.now().plusDays(14));
            loanRepo.save(loan);

            catalogClient.updateCatalogServiceForLoan(copyId, token, loan);

            return loan;
        }
        throw new CopyAlreadyBorrowedException("Book cannot be loaned");
    }

    @Transactional
    public Loan returnBook(int copyId, String userId) {
        Loan loan = loanRepo.findByCopyIdAndLoanStatus(copyId, LoanStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("No active loan found for this copy and user"));

        if(loan.getUserId() == null || !loan.getUserId().equals(userId)) {
            throw new RuntimeException("This user did not borrow this copy");
        }

        loan.setLoanStatus(LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());

        //calculate fine if returned after due date
        if (loan.getReturnDate().isAfter(loan.getDueDate())) {
            long daysLate = loan.getReturnDate().toEpochDay() - loan.getDueDate().toEpochDay();
            double fineAmount = daysLate * 1.0; // Assuming $1 fine per day late
            loan.setFineAmount(fineAmount);
            loan.setFineStatus("PENDING");

            // Here you would typically publish the event to a message broker or event bus
            try {
                String payload = objectMapper.writeValueAsString(new FineGeneratedEvent(loan.getId(), fineAmount, loan.getFineStatus()));

                OutboxEvent outboxEvent = new OutboxEvent(
                        "Loan",
                        String.valueOf(loan.getId()),
                        "FineGenerated",
                        payload, // You might want to serialize this properly
                        "circulation-events"
                );
                outboxRepo.save(outboxEvent);
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize outbox event", e);
            }
        }

        loanRepo.save(loan);



        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        String token = jwt.getTokenValue();

        catalogClient.updateCatalogServiceForReturn(copyId, token, loan);

        return loan;
    }
}