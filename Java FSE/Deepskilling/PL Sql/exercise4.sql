-- SET SERVEROUTPUT ON for Oracle SQL Developer / Live SQL execution
SET SERVEROUTPUT ON;

-- ==========================================
-- Exercise 4: Functions
-- ==========================================

-- Scenario 1: Calculate the age of customers for eligibility checks
CREATE OR REPLACE FUNCTION CalculateAge(
    p_DOB IN DATE
) RETURN NUMBER IS
    v_Age NUMBER;
BEGIN
    IF p_DOB IS NULL THEN
        RETURN NULL;
    END IF;
    
    v_Age := FLOOR(MONTHS_BETWEEN(SYSDATE, p_DOB) / 12);
    RETURN v_Age;
END CalculateAge;
/

-- Scenario 2: Calculate the monthly installment for a loan (EMI)
-- Formula: EMI = P * r * (1 + r)^n / ((1 + r)^n - 1)
-- P = Principal (p_LoanAmount), r = Monthly interest rate (p_InterestRate / 12 / 100), n = Total payments (p_DurationYears * 12)
CREATE OR REPLACE FUNCTION CalculateMonthlyInstallment(
    p_LoanAmount IN NUMBER,
    p_InterestRate IN NUMBER, -- annual rate, e.g. 5.5 for 5.5%
    p_DurationYears IN NUMBER
) RETURN NUMBER IS
    v_MonthlyRate NUMBER;
    v_TotalMonths NUMBER;
    v_Installment NUMBER;
BEGIN
    -- Validation checks
    IF p_LoanAmount <= 0 OR p_InterestRate < 0 OR p_DurationYears <= 0 THEN
        RETURN 0;
    END IF;

    v_MonthlyRate := (p_InterestRate / 100) / 12;
    v_TotalMonths := p_DurationYears * 12;

    IF v_MonthlyRate = 0 THEN
        v_Installment := p_LoanAmount / v_TotalMonths;
    ELSE
        v_Installment := (p_LoanAmount * v_MonthlyRate * POWER(1 + v_MonthlyRate, v_TotalMonths)) / 
                         (POWER(1 + v_MonthlyRate, v_TotalMonths) - 1);
    END IF;

    RETURN ROUND(v_Installment, 2);
END CalculateMonthlyInstallment;
/

-- Scenario 3: Check if a customer has sufficient balance before making a transaction
-- Returns BOOLEAN for PL/SQL block usage
CREATE OR REPLACE FUNCTION HasSufficientBalance(
    p_AccountID IN NUMBER,
    p_Amount IN NUMBER
) RETURN BOOLEAN IS
    v_Balance NUMBER;
BEGIN
    IF p_Amount < 0 THEN
        RETURN FALSE;
    END IF;
    
    SELECT Balance INTO v_Balance 
    FROM Accounts 
    WHERE AccountID = p_AccountID;
    
    IF v_Balance >= p_Amount THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN FALSE;
    WHEN OTHERS THEN
        RETURN FALSE;
END HasSufficientBalance;
/

-- Verification Script / Example of usage
DECLARE
    v_Age NUMBER;
    v_EMI NUMBER;
    v_HasFunds BOOLEAN;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Testing Functions ---');
    
    -- Test CalculateAge
    v_Age := CalculateAge(TO_DATE('1990-07-20', 'YYYY-MM-DD'));
    DBMS_OUTPUT.PUT_LINE('Age for DOB 1990-07-20: ' || v_Age || ' years');
    
    -- Test CalculateMonthlyInstallment
    v_EMI := CalculateMonthlyInstallment(10000, 6, 3);
    DBMS_OUTPUT.PUT_LINE('Monthly Installment for $10,000 Loan @ 6% interest for 3 years: $' || v_EMI);

    -- Test HasSufficientBalance
    v_HasFunds := HasSufficientBalance(1, 500);
    IF v_HasFunds THEN
        DBMS_OUTPUT.PUT_LINE('Account ID 1 has at least $500: Yes');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Account ID 1 has at least $500: No');
    END IF;
END;
/
