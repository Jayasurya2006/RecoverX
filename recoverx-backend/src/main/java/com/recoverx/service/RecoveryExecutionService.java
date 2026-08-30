package com.recoverx.service;

import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Executes one recovery attempt. Today this is a weighted coin flip standing
 * in for a real payment gateway call - swap attempt() for a Razorpay
 * test-mode API call and nothing upstream (orchestrator, policy gate,
 * controller) needs to change.
 */
@Service
public class RecoveryExecutionService {

    private final Random random = new Random();

    public boolean attempt(double successProbability) {
        return random.nextDouble() < successProbability;
    }
}
