package com.concord.circulationservice.service;

import com.concord.circulationservice.dto.CopyResponse;
import com.concord.circulationservice.entity.Loan;
import com.concord.circulationservice.entity.LoanStatus;
import com.concord.circulationservice.exception.BorrowFailedException;
import com.concord.circulationservice.exception.CatalogServiceUnavailableException;
import com.concord.circulationservice.exception.CopyNotFoundException;
import com.concord.circulationservice.repository.LoanRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class CatalogClient {

    private final WebClient catalogWebClient;
    private final LoanRepository loanRepo;

    public CatalogClient(WebClient catalogWebClient, LoanRepository loanRepo) {
        this.catalogWebClient = catalogWebClient;
        this.loanRepo = loanRepo;
    }

    @CircuitBreaker(name = "catalogService", fallbackMethod = "getCopyFromCatalogServiceFallback")
    public CopyResponse getCopyFromCatalogService(int copyId, String token) {
        CopyResponse copy;
        try {
            copy = catalogWebClient.get()
                    .uri("/api/copies/{id}", copyId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, response ->
                            Mono.error(new CopyNotFoundException("Copy not found: " + copyId)))
                    .bodyToMono(CopyResponse.class)
                    .block();
        } catch (CopyNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CatalogServiceUnavailableException("Catalog Service is unavailable", e);
        }
        return copy;
    }

    public CopyResponse getCopyFromCatalogServiceFallback(int copyId, String token, Throwable t) {
        throw new CatalogServiceUnavailableException("Catalog Service is unavailable, FALLBACK HIT", t);
    }

    @CircuitBreaker(name = "catalogService", fallbackMethod = "updateCatalogServiceForLoanFallback")
    public void updateCatalogServiceForLoan(int copyId, String token, Loan loan) {
        try {
            catalogWebClient.patch()
                    .uri("/api/copies/{id}/status", copyId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .bodyValue(Map.of("status", "BORROWED"))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            loan.setLoanStatus(LoanStatus.CANCELLED);
            loanRepo.save(loan);
            throw new BorrowFailedException("Error occurred while borrowing book", e);
        }
    }

    public void updateCatalogServiceForLoanFallback(int copyId, String token, Loan loan, Throwable t) {
        loan.setLoanStatus(LoanStatus.CANCELLED);
        loanRepo.save(loan);
        throw new CatalogServiceUnavailableException("Catalog Service is unavailable", t);
    }

    @CircuitBreaker(name = "catalogService", fallbackMethod = "updateCatalogServiceForReturnFallback")
    public void updateCatalogServiceForReturn(int copyId, String token, Loan loan) {
        try {
            catalogWebClient.patch()
                    .uri("/api/copies/{id}/status", copyId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .bodyValue(Map.of("status", "AVAILABLE"))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            loan.setLoanStatus(LoanStatus.ACTIVE);
            loanRepo.save(loan);

            throw new CatalogServiceUnavailableException("Error occurred while returning book", e);
        }
    }


    public void updateCatalogServiceForReturnFallback(int copyId, String token, Loan loan, Throwable t) {
        loan.setLoanStatus(LoanStatus.ACTIVE);
        loanRepo.save(loan);
        throw new CatalogServiceUnavailableException("Catalog Service is unavailable, FALLBACK HIT", t);
    }
}