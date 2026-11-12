package com.concord.circulationservice.service;

import com.concord.circulationservice.dto.CopyResponse;
import com.concord.circulationservice.entity.Loan;
import com.concord.circulationservice.entity.LoanStatus;
import com.concord.circulationservice.entity.OutboxEvent;
import com.concord.circulationservice.exception.CopyAlreadyBorrowedException;
import com.concord.circulationservice.repository.LoanRepository;
import com.concord.circulationservice.repository.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepo;

    @Mock
    private OutboxEventRepository outboxRepo;

    @Mock
    private CatalogClient catalogClient;

    @InjectMocks
    private LoanService loanService;

    @Test
    void borrowBook_whenCopyIsAvailable_savesLoanAndReturnIt() {
        // Implement test logic here
        //Arrange
        int copyId = 1;
        String userId = "user123";
        String token = "mockedToken";

        CopyResponse availableCopy = new CopyResponse();
        availableCopy.setStatus("AVAILABLE");

        when(loanRepo.existsByCopyIdAndLoanStatus(copyId, LoanStatus.ACTIVE)).thenReturn(false);
        when(catalogClient.getCopyFromCatalogService(eq(copyId), anyString())).thenReturn(availableCopy);

        // Act
        Loan result = loanService.borrowBook(copyId, userId, token);

        // Assert
        assertNotNull(result);
        assertEquals(LoanStatus.ACTIVE, result.getLoanStatus());
        verify(loanRepo).save(any(Loan.class));
    }

    @Test
    void borrowBook_whenCopyIsBorrowed_throwsException() {
        // Arrange
        int copyId = 1;
        String userId = "user123";
        String token = "mockedToken";

        CopyResponse borrowedCopy = new CopyResponse();
        borrowedCopy.setStatus("BORROWED");

        when(loanRepo.existsByCopyIdAndLoanStatus(copyId, LoanStatus.ACTIVE)).thenReturn(false);
        when(catalogClient.getCopyFromCatalogService(eq(copyId), anyString())).thenReturn(borrowedCopy);

        // Act & Assert
        assertThrows(CopyAlreadyBorrowedException.class, () -> loanService.borrowBook(copyId, userId, token));
    }

    @Test
    void borrowBook_whenCopyIsAlreadyBorrowedInLoanRepo_throwsException() {
        // Arrange
        int copyId = 1;
        String userId = "user123";
        String token = "mockedToken";

        when(loanRepo.existsByCopyIdAndLoanStatus(copyId, LoanStatus.ACTIVE)).thenReturn(true);

        // Act & Assert
        assertThrows(CopyAlreadyBorrowedException.class, () -> loanService.borrowBook(copyId, userId, token));
    }

    @Test
    void returnBook_whenNoActiveLoanExists_throwsException() {
        // Arrange
        int copyId = 1;
        String userId = "user123";
        String token = "mockedToken";

        when(loanRepo.findByCopyIdAndLoanStatus(copyId, LoanStatus.ACTIVE)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> loanService.returnBook(copyId, userId, token));
    }

    @Test
    void returnBook_whenUserDidNotBorrowCopy_throwsException() {
        // Arrange
        int copyId = 1;
        String userId = "user123";
        String token = "mockedToken";

        Loan loan = new Loan();
        loan.setUserId("differentUser");
        loan.setCopyId(copyId);
        loan.setLoanStatus(LoanStatus.ACTIVE);

        when(loanRepo.findByCopyIdAndLoanStatus(copyId, LoanStatus.ACTIVE)).thenReturn(Optional.of(loan));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> loanService.returnBook(copyId, userId, token));
    }

    @Test
    void returnBook_failedToSerializeOutboxEventAfterCalculateFine_shouldThrowRuntimeException() {

        //Failed to serialize outbox event
        // Arrange
        int copyId = 1;
        String userId = "user123";
        String token = "mockedToken";
        BigDecimal fineAmount = BigDecimal.valueOf(5.0); // Example fine amount


        Loan loan = new Loan();
        loan.setUserId(userId);
        loan.setCopyId(copyId);
        loan.setLoanStatus(LoanStatus.RETURNED);
        loan.setFineAmount(fineAmount);
        loan.setFineStatus("PENDING");
        loan.setDueDate(LocalDate.now().minusDays(5)); // overdue, forces the fine/outbox branch to execute

        when(loanRepo.findByCopyIdAndLoanStatus(copyId, LoanStatus.ACTIVE)).thenReturn(Optional.of(loan));

        String payload = "invalidPayload"; // Simulate an invalid payload that will cause serialization to fail
        OutboxEvent outboxEvent = new OutboxEvent(
                "Loan",
                String.valueOf(loan.getId()),
                "FineGenerated",
                payload,
                "circulation-events"
        );

        when(outboxRepo.save(any(OutboxEvent.class))).thenThrow(new RuntimeException("Failed to serialize outbox event"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> loanService.returnBook(copyId, userId, token));
    }

    @Test
    void returnBook_whenValidLoanExistsAndUserMatches_shouldReturnUpdatedLoan() {
        // Arrange
        int copyId = 1;
        String userId = "user123";
        String token = "mockedToken";

        Loan loan = new Loan();
        loan.setUserId(userId);
        loan.setCopyId(copyId);
        loan.setLoanStatus(LoanStatus.ACTIVE);
        loan.setDueDate(LocalDate.now().plusDays(5)); // due in the future, no fine triggered

        when(loanRepo.findByCopyIdAndLoanStatus(copyId, LoanStatus.ACTIVE)).thenReturn(Optional.of(loan));
        when(loanRepo.save(any(Loan.class))).thenReturn(loan);

        // Act
        Loan result = loanService.returnBook(copyId, userId, token);

        // Assert
        assertNotNull(result);
        assertEquals(LoanStatus.RETURNED, result.getLoanStatus());
        verify(loanRepo).save(any(Loan.class));

    }
}