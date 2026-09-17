package com.template.model.challenge;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado da execução de uma suíte de testes de um desafio de programação.
 */
public class TestResult {
    private final boolean allPassed;
    private final int totalTests;
    private final int passedTests;
    private final long executionTimeMs;
    private final List<String> logs;
    private final String errorMessage;

    public TestResult(boolean allPassed, int totalTests, int passedTests, long executionTimeMs, List<String> logs, String errorMessage) {
        this.allPassed = allPassed;
        this.totalTests = totalTests;
        this.passedTests = passedTests;
        this.executionTimeMs = executionTimeMs;
        this.logs = (logs != null) ? logs : new ArrayList<>();
        this.errorMessage = errorMessage;
    }

    public static TestResult failure(String errorMessage, List<String> logs, long executionTimeMs) {
        return new TestResult(false, 1, 0, executionTimeMs, logs, errorMessage);
    }

    public boolean isAllPassed() {
        return allPassed;
    }

    public int getTotalTests() {
        return totalTests;
    }

    public int getPassedTests() {
        return passedTests;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public List<String> getLogs() {
        return logs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
