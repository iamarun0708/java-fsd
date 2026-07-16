package FinancialForecasting;

/**
 * Exercise 7: Financial Forecasting
 * 
 * Uses recursion to predict future values based on past growth rates.
 * Includes optimization with memoization.
 */

import java.util.HashMap;

public class FinancialForecastTest {

    /**
     * Recursive Future Value Calculator
     * 
     * Formula: FV = PV * (1 + growthRate)^years
     * Recursive approach: FV(n) = FV(n-1) * (1 + growthRate)
     * 
     * Time Complexity: O(n) - n recursive calls
     * Space Complexity: O(n) - n stack frames
     */
    public static double calculateFutureValue(double presentValue, double growthRate, int years) {
        // Base case
        if (years == 0) {
            return presentValue;
        }
        // Recursive case: grow value for one year, then recurse
        return calculateFutureValue(presentValue * (1 + growthRate), growthRate, years - 1);
    }

    /**
     * Recursive Compound Interest with Year-by-Year Output
     */
    public static double calculateWithDetails(double presentValue, double growthRate, int years, int currentYear) {
        // Base case
        if (currentYear > years) {
            return presentValue;
        }

        double newValue = presentValue * (1 + growthRate);
        System.out.printf("  Year %2d: $%,.2f -> $%,.2f (growth: +$%,.2f)%n",
                currentYear, presentValue, newValue, newValue - presentValue);

        // Recursive call for next year
        return calculateWithDetails(newValue, growthRate, years, currentYear + 1);
    }

    /**
     * Fibonacci-based Growth Prediction (demonstrates memoization)
     * 
     * Without memoization: O(2^n) - exponential!
     * With memoization: O(n) - each value computed once
     */
    static HashMap<Integer, Long> memo = new HashMap<>();

    public static long fibonacciMemoized(int n) {
        if (n <= 1) return n;
        if (memo.containsKey(n)) return memo.get(n);

        long result = fibonacciMemoized(n - 1) + fibonacciMemoized(n - 2);
        memo.put(n, result);
        return result;
    }

    public static long fibonacciNaive(int n) {
        if (n <= 1) return n;
        return fibonacciNaive(n - 1) + fibonacciNaive(n - 2);
    }

    public static void main(String[] args) {
        System.out.println("=== Exercise 7: Financial Forecasting ===\n");

        /*
         * Recursion:
         * - A function that calls itself
         * - Must have a base case (stopping condition)
         * - Must progress towards the base case
         * 
         * Advantages:
         * - Simplifies complex problems
         * - Elegant code for naturally recursive problems
         * 
         * Disadvantages:
         * - Stack overflow risk for deep recursion
         * - Can be inefficient without optimization (memoization)
         * - Higher memory usage (call stack)
         * 
         * Optimization:
         * - Memoization: Cache results of expensive calls
         * - Tail recursion: Some compilers optimize tail calls
         */

        // --- Simple Future Value ---
        double initialInvestment = 10000.00;
        double annualGrowthRate = 0.08; // 8%
        int forecastYears = 10;

        System.out.println("--- Future Value Prediction ---");
        System.out.printf("  Initial Investment: $%,.2f%n", initialInvestment);
        System.out.printf("  Annual Growth Rate: %.0f%%%n", annualGrowthRate * 100);
        System.out.printf("  Forecast Period:    %d years%n%n", forecastYears);

        double futureValue = calculateFutureValue(initialInvestment, annualGrowthRate, forecastYears);
        System.out.printf("  Future Value after %d years: $%,.2f%n", forecastYears, futureValue);
        System.out.printf("  Total Growth: $%,.2f (%.1f%%)%n%n",
                futureValue - initialInvestment,
                ((futureValue - initialInvestment) / initialInvestment) * 100);

        // --- Year-by-Year Breakdown ---
        System.out.println("--- Year-by-Year Growth (5 years at 10%) ---");
        double result = calculateWithDetails(5000.00, 0.10, 5, 1);
        System.out.printf("  Final Value: $%,.2f%n%n", result);

        // --- Fibonacci Optimization Demo ---
        System.out.println("--- Recursion Optimization: Fibonacci ---");
        System.out.println("  Demonstrating memoization vs naive recursion:\n");

        int fibN = 40;

        // Memoized version
        long startTime = System.nanoTime();
        long fibResult = fibonacciMemoized(fibN);
        long memoTime = System.nanoTime() - startTime;
        System.out.printf("  Fibonacci(%d) = %d%n", fibN, fibResult);
        System.out.printf("  Memoized: %.3f ms%n", memoTime / 1_000_000.0);

        // Naive version (will be much slower)
        startTime = System.nanoTime();
        long fibNaive = fibonacciNaive(fibN);
        long naiveTime = System.nanoTime() - startTime;
        System.out.printf("  Naive:    %.3f ms%n", naiveTime / 1_000_000.0);
        System.out.printf("  Speedup:  %.0fx faster with memoization!%n", (double) naiveTime / memoTime);

        System.out.println("\n✓ Memoization reduces exponential O(2^n) to linear O(n)!");
    }
}
