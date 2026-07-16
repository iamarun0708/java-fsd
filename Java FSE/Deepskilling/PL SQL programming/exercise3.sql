-- SET SERVEROUTPUT ON for Oracle SQL Developer / Live SQL execution
SET SERVEROUTPUT ON;

-- ==========================================
-- Exercise 3: Stored Procedures
-- ==========================================

-- Scenario 1: Process monthly interest for all savings accounts
CREATE OR REPLACE PROCEDURE ProcessMonthlyInterest AS
    v_UpdatedCount NUMBER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- ProcessMonthlyInterest Execution ---');
    
    -- Update balances of all savings accounts by adding 1% interest
    UPDATE Accounts
    SET Balance = Balance * 1.01,
        LastModified = SYSDATE
    WHERE AccountType = 'Savings';
    
    v_UpdatedCount := SQL%ROWCOUNT;
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Success: Monthly interest applied to ' || v_UpdatedCount || ' Savings account(s).');
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Error: Failed to process monthly interest: ' || SQLERRM);
END;
/

-- Scenario 2: Update employee salary/bonus based on performance for a department
CREATE OR REPLACE PROCEDURE UpdateEmployeeBonus(
    p_Department IN VARCHAR2,
    p_BonusPercentage IN NUMBER
) AS
    v_UpdatedCount NUMBER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- UpdateEmployeeBonus Execution ---');
    
    IF p_BonusPercentage < 0 THEN
        DBMS_OUTPUT.PUT_LINE('Error: Bonus percentage cannot be negative.');
        RETURN;
    END IF;

    -- Update salaries for employees in the specified department
    UPDATE Employees
    SET Salary = Salary * (1 + p_BonusPercentage / 100)
    WHERE Department = p_Department;
    
    v_UpdatedCount := SQL%ROWCOUNT;
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Success: Applied a bonus of ' || p_BonusPercentage || '% to ' || 
                         v_UpdatedCount || ' employee(s) in the ' || p_Department || ' department.');
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Error: Failed to update employee bonus: ' || SQLERRM);
END;
/

-- Scenario 3: Transfer funds between accounts with sufficient balance check
CREATE OR REPLACE PROCEDURE TransferFunds(
    p_SourceAccountID IN NUMBER,
    p_DestAccountID IN NUMBER,
    p_Amount IN NUMBER
) AS
    v_SourceBalance NUMBER;
    v_SourceCount NUMBER;
    v_DestCount NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TransferFunds Execution ---');
    
    -- Check transfer amount
    IF p_Amount <= 0 THEN
        DBMS_OUTPUT.PUT_LINE('Error: Transfer amount must be greater than zero.');
        RETURN;
    END IF;

    -- Check if both accounts exist
    SELECT COUNT(*) INTO v_SourceCount FROM Accounts WHERE AccountID = p_SourceAccountID;
    SELECT COUNT(*) INTO v_DestCount FROM Accounts WHERE AccountID = p_DestAccountID;
    
    IF v_SourceCount = 0 OR v_DestCount = 0 THEN
        DBMS_OUTPUT.PUT_LINE('Error: Source or Destination account not found.');
        RETURN;
    END IF;

    -- Get current balance of source account
    SELECT Balance INTO v_SourceBalance FROM Accounts WHERE AccountID = p_SourceAccountID;
    
    -- Verify balance
    IF v_SourceBalance < p_Amount THEN
        DBMS_OUTPUT.PUT_LINE('Error: Insufficient balance. Available: $' || v_SourceBalance || 
                             ' | Requested: $' || p_Amount);
        RETURN;
    END IF;

    -- Deduct from source and add to destination
    UPDATE Accounts
    SET Balance = Balance - p_Amount, LastModified = SYSDATE
    WHERE AccountID = p_SourceAccountID;
    
    UPDATE Accounts
    SET Balance = Balance + p_Amount, LastModified = SYSDATE
    WHERE AccountID = p_DestAccountID;
    
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Success: Transferred $' || p_Amount || ' from Account ID ' || p_SourceAccountID || 
                         ' to Account ID ' || p_DestAccountID);
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Error: Unexpected error during fund transfer: ' || SQLERRM);
END;
/
