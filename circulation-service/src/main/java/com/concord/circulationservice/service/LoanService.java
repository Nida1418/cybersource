package com.concord.circulationservice.service;

import com.concord.circulationservice.dto.CopyResponse;
import com.concord.circulationservice.entity.Loan;
import com.concord.circulationservice.entity.LoanStatus;
import com.concord.circulationservice.exception.BorrowFailedException;
import com.concord.circulationservice.exception.CatalogServiceUnavailableException;
import com.concord.circulationservice.exception.CopyAlreadyBorrowedException;
import com.concord.circulationservice.exception.CopyNotFoundException;
import com.concord.circulationservice.repository.LoanRepository;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class LoanService {

    private final LoanRepository loanRepo;
    private final WebClient catalogWebClient;

    public LoanService(LoanRepository loanRepo, WebClient catalogWebClient) {
        this.loanRepo = loanRepo;
        this.catalogWebClient = catalogWebClient;
    }

    public Loan borrowBook(int copyId, String userId) {

        boolean copyIsAvailable = !(loanRepo.existsByCopyIdAndLoanStatus(copyId, LoanStatus.ACTIVE));

        if (!copyIsAvailable) {
            throw new CopyAlreadyBorrowedException("Book is already borrowed");
        }

        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        String token = jwt.getTokenValue();
        CopyResponse copy = new CopyResponse();

        try{
            // WebClient call to Catalog Service to get the copy
            copy = catalogWebClient.get()
                    .uri("/api/copies/{id}", copyId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, response -> {
                        return Mono.error(new CopyNotFoundException("Copy not found: " + copyId));
                    })
                    .bodyToMono(CopyResponse.class)
                    .block();

        } catch (CopyNotFoundException e) {
            throw e; // rethrow the exception to be handled by the global exception handler
        }
        catch (Exception e) {
            throw new CatalogServiceUnavailableException("Catalog Service is unavailable", e);
        }


        if ("BORROWED".equals(copy.getStatus())) {
            throw new CopyAlreadyBorrowedException("Book is already borrowed");
        } else if ("AVAILABLE".equals(copy.getStatus())) {
            // build the loan row and loanRepo.save(loan)
            Loan loan = new Loan();
            loan.setCopyId(copyId);
            loan.setUserId(userId);
            loan.setLoanStatus(LoanStatus.ACTIVE);
            loanRepo.save(loan);

            try{
                // PATCH call to Catalog Service to change status to "BORROWED"
                catalogWebClient.patch()
                        .uri("/api/copies/{id}/status", copyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .bodyValue(Map.of("status", "BORROWED"))
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block();
            } catch(Exception e) {
                loan.setLoanStatus(LoanStatus.CANCELLED);
                loanRepo.save(loan);
                throw new BorrowFailedException("Error occurred while borrowing book",e);
            }

            return loan;
        }
        throw new CopyAlreadyBorrowedException("Book cannot be loaned"); //if status is neither BORROWED nor AVAILABLE, throw exception

    }
}