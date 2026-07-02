-- SET SERVEROUTPUT ON for Oracle SQL Developer / Live SQL execution
SET SERVEROUTPUT ON;

-- ==========================================
-- Exercise 6: Cursors
-- ==========================================

-- Scenario 1: Generate monthly statements for all customers
DECLARE
    -- Cursor to fetch all transactions for the current month
    CURSOR GenerateMonthlyStatements IS
        SELECT c.Name, 
               a.AccountID, 
               t.TransactionID, 
               t.TransactionDate, 
               t.Amount, 
               t.TransactionType
        FROM Customers c
        JOIN Accounts a ON c.CustomerID = a.CustomerID
        JOIN Transactions t ON a.AccountID = t.AccountID
        WHERE t.TransactionDate >= TRUNC(SYSDATE, 'MM') 
          AND t.TransactionDate < ADD_MONTHS(TRUNC(SYSDATE, 'MM'), 1);
          
    v_StatementCount NUMBER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Scenario 1: Generating Monthly Statements ---');
    
    FOR rec IN GenerateMonthlyStatements LOOP
        v_StatementCount := v_StatementCount + 1;
        DBMS_OUTPUT.PUT_LINE('Customer: ' || RPAD(rec.Name, 15) || 
                             ' | Account ID: ' || rec.AccountID || 
                             ' | Tx ID: ' || rec.TransactionID || 
                             ' | Date: ' || TO_CHAR(rec.TransactionDate, 'YYYY-MM-DD') || 
                             ' | Type: ' || RPAD(rec.TransactionType, 10) || 
                             ' | Amount: $' || rec.Amount);
    END LOOP;
    
    IF v_StatementCount = 0 THEN
        DBMS_OUTPUT.PUT_LINE('No transactions found for the current month.');
    END IF;
    
    DBMS_OUTPUT.PUT_LINE('Total Statements Generated: ' || v_StatementCount || CHR(10));
END;
/

-- Scenario 2: Apply an annual fee to all accounts
DECLARE
    v_AnnualFee CONSTANT NUMBER := 20.00;
    
    -- Explicit cursor to select all accounts and lock their rows for update
    CURSOR ApplyAnnualFee IS
        SELECT AccountID, Balance
        FROM Accounts
        FOR UPDATE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Scenario 2: Applying Annual Maintenance Fee ---');
    
    FOR rec IN ApplyAnnualFee LOOP
        UPDATE Accounts
        SET Balance = Balance - v_AnnualFee,
            LastModified = SYSDATE
        WHERE CURRENT OF ApplyAnnualFee;
        
        DBMS_OUTPUT.PUT_LINE('Account ID: ' || rec.AccountID || 
                             ' | Original Balance: $' || rec.Balance || 
                             ' | New Balance (after $' || v_AnnualFee || ' fee): $' || (rec.Balance - v_AnnualFee));
    END LOOP;
    
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Annual Fee Applied Successfully to all Accounts.' || CHR(10));
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Error applying annual fee: ' || SQLERRM || CHR(10));
END;
/

-- Scenario 3: Update the interest rate for all loans based on a new policy
-- Policy: If LoanAmount > $5000, new rate is 5.5%. Otherwise, new rate is 6.5%.
DECLARE
    -- Explicit cursor to select all loans and lock their rows for update
    CURSOR UpdateLoanInterestRates IS
        SELECT LoanID, LoanAmount, InterestRate
        FROM Loans
        FOR UPDATE;
        
    v_NewRate NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Scenario 3: Updating Loan Interest Rates (New Policy) ---');
    
    FOR rec IN UpdateLoanInterestRates LOOP
        -- Apply Policy rules
        IF rec.LoanAmount > 5000 THEN
            v_NewRate := 5.5;
        ELSE
            v_NewRate := 6.5;
        END IF;
        
        UPDATE Loans
        SET InterestRate = v_NewRate
        WHERE CURRENT OF UpdateLoanInterestRates;
        
        DBMS_OUTPUT.PUT_LINE('Loan ID: ' || rec.LoanID || 
                             ' | Amount: $' || rec.LoanAmount || 
                             ' | Old Rate: ' || rec.InterestRate || '% -> New Rate: ' || v_NewRate || '%');
    END LOOP;
    
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Loan Interest Rates Updated Successfully.');
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Error updating loan interest rates: ' || SQLERRM);
END;
/
