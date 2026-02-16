package com.mohit.durable;

import java.util.function.Supplier;

public class DurableExecutor {

    private final String workflowId;
    private final StepStore stepStore;
    private int sequenceNumber = 0;

    public DurableExecutor(String workflowId) {
        this.workflowId = workflowId;
        this.stepStore = new StepStore();
    }

    public String step(String stepName, Supplier<String> action) {

        sequenceNumber++;

        // If step already executed → return stored result (REPLAY)
        if (stepStore.stepExists(workflowId, sequenceNumber)) {
            System.out.println("Replaying step: " + stepName);
            return stepStore.getStepResult(workflowId, sequenceNumber);
        }

        // Otherwise execute and persist
        System.out.println("Executing step: " + stepName);
        String result = action.get();

        stepStore.saveStep(workflowId, stepName, sequenceNumber, result);

        return result;
    }
}
