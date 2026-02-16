package com.mohit.durable;

public class App {

    public static void main(String[] args) {

        DurableExecutor executor = new DurableExecutor("workflow-1");
        StepStore store = new StepStore();

        boolean step2AlreadyExecuted = store.stepExists("workflow-1", 2);

        executor.step("step-1", () -> {
            System.out.println("Performing Step 1 work...");
            return "Result-1";
        });

        executor.step("step-2", () -> {
            System.out.println("Performing Step 2 work...");
            return "Result-2";
        });

        // Crash ONLY if step-2 was NOT previously executed
        if (!step2AlreadyExecuted) {
            System.out.println("Simulating crash...");
            System.exit(1);
        }

        executor.step("step-3", () -> {
            System.out.println("Performing Step 3 work...");
            return "Result-3";
        });

        System.out.println("Workflow completed.");
    }
}
