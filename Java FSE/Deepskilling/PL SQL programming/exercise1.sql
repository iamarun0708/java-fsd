-- SET SERVEROUTPUT ON for Oracle SQL Developer / Live SQL execution
SET SERVEROUTPUT ON;

-- ==========================================
-- Exercise 1: Control Structures
-- ==========================================

-- Scenario 1: Applying 1% discount to loan interest rates for customers above 60 years old
DECLARE
    CURSOR c_over_60 IS
        SELECT c.CustomerID, c.Name, c.DOB, l.LoanID, l.InterestRate
        FROM Customers c
        JOIN Loans l ON c.CustomerID = l.CustomerID
        WHERE MONTHS_BETWEEN(SYSDATE, c.DOB) / 12 > 60;
    
    v_age NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Scenario 1: Applying Interest Rate Discount for Senior Customers ---');
    FOR rec IN c_over_60 LOOP
        v_age := FLOOR(MONTHS_BETWEEN(SYSDATE, rec.DOB) / 12);
        UPDATE Loans
        SET InterestRate = InterestRate - 1
        WHERE LoanID = rec.LoanID;
        
        DBMS_OUTPUT.PUT_LINE('Customer: ' || rec.Name || ' (Age: ' || v_age || 
                             ') | Loan ID: ' || rec.LoanID || 
                             ' | Interest Rate reduced from ' || rec.InterestRate || '% to ' || (rec.InterestRate - 1) || '%');
    END LOOP;
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Scenario 1 Completed Successfully.' || CHR(10));
END;
/

-- Scenario 2: Promote customers to VIP status based on balance (> $10,000)
DECLARE
    CURSOR c_vip_candidates IS
        SELECT CustomerID, Name, Balance, IsVIP
        FROM Customers;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Scenario 2: Promoting Customers to VIP Status ---');
    FOR rec IN c_vip_candidates LOOP
        IF rec.Balance > 10000 THEN
            UPDATE Customers
            SET IsVIP = 'TRUE'
            WHERE CustomerID = rec.CustomerID;
            
            DBMS_OUTPUT.PUT_LINE('Customer: ' || rec.Name || ' | Balance: $' || rec.Balance || 
                                 ' | Status: Promoted to VIP');
        ELSE
            DBMS_OUTPUT.PUT_LINE('Customer: ' || rec.Name || ' | Balance: $' || rec.Balance || 
                                 ' | Status: Normal');
        END IF;
    END LOOP;
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Scenario 2 Completed Successfully.' || CHR(10));
END;
/

-- Scenario 3: Send reminders to customers whose loans are due within the next 30 days
DECLARE
    CURSOR c_due_loans IS
        SELECT c.Name, l.LoanID, l.EndDate
        FROM Loans l
        JOIN Customers c ON l.CustomerID = c.CustomerID
        WHERE l.EndDate BETWEEN SYSDATE AND SYSDATE + 30;
        
    v_found BOOLEAN := FALSE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Scenario 3: Loan Due Reminders (Next 30 Days) ---');
    FOR rec IN c_due_loans LOOP
        v_found := TRUE;
        DBMS_OUTPUT.PUT_LINE('REMINDER: Dear ' || rec.Name || ', your Loan (ID: ' || rec.LoanID || 
                             ') is due on ' || TO_CHAR(rec.EndDate, 'YYYY-MM-DD') || 
                             '. Please ensure timely repayment.');
    END LOOP;
    
    IF NOT v_found THEN
        DBMS_OUTPUT.PUT_LINE('No loans are due within the next 30 days.');
    END IF;
    DBMS_OUTPUT.PUT_LINE('Scenario 3 Completed Successfully.');
END;
/
