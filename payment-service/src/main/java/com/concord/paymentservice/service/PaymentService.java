package com.concord.paymentservice.service;

import com.concord.paymentservice.entity.Payment;
import com.concord.paymentservice.entity.PaymentStatus;
import com.concord.paymentservice.exception.PaymentNotFoundException;
import com.concord.paymentservice.repository.PaymentRepository;
import com.concord.paymentservice.response.PaymentGatewayResponse;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentGateway paymentGateway;
    private final PaymentRepository paymentRepository;


    public PaymentService(PaymentGateway paymentGateway, PaymentRepository paymentRepository) {
        this.paymentGateway = paymentGateway;
        this.paymentRepository = paymentRepository;

    }


    public void payFine(int loanId) {
        // Implement the logic to process the fine payment for the given loanId


        // Process the new payment
        // You can call the paymentGateway to process the payment and save the payment details in the database
        //Step 1: Retrieve the payment record for the given loanId
        Payment payment = (paymentRepository.findByLoanId(loanId).orElseThrow(() -> new PaymentNotFoundException("Payment not found for loanId: " + loanId)));

        //Step 2: Check if the payment status is already PAYMENT_IN_PROGRESS, PAID, or FAILED
        if (payment.getStatus() == PaymentStatus.PAYMENT_IN_PROGRESS || payment.getStatus() == PaymentStatus.PAID || payment.getStatus() == PaymentStatus.FAILED) {
            // Handle the case where the payment is already marked as PAYMENT_IN_PROGRESS
            return;
        }

        //Step 3: Save the payment status as PAYMENT_IN_PROGRESS
        String paymentToken = "your_payment_token"; // Replace with your actual payment token
        payment.setStatus(PaymentStatus.PAYMENT_IN_PROGRESS);
        paymentRepository.save(payment);

        //Step 4: Call the payment gateway to process the payment
        PaymentGatewayResponse paymentGatewayResponse = paymentGateway.processPayment(loanId, payment.getAmount(), paymentToken, payment.getCurrency());

        //Step 5: Update the payment status based on the response from the payment gateway
        if (paymentGatewayResponse.isSuccess()) {
            payment.setStatus(PaymentStatus.PAID);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        //Step 6: Save the updated payment record in the database
        paymentRepository.save(payment);
    }

}
