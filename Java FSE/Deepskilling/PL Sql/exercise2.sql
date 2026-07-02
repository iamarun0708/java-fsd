-- SET SERVEROUTPUT ON for Oracle SQL Developer / Live SQL execution
SET SERVEROUTPUT ON;

-- ==========================================
-- Exercise 2: Error Handling
-- ==========================================

-- Scenario 1: SafeTransferFunds procedure with exception handling and transaction rollback
CREATE OR REPLACE PROCEDURE SafeTransferFunds(
    p_SourceAccountID IN NUMBER,
    p_DestAccountID IN NUMBER,
    p_Amount IN NUMBER
) AS
    v_SourceBalance NUMBER;
    v_SourceCount NUMBER;
    v_DestCount NUMBER;
    v_NextTxID NUMBER;
    
    -- Custom Exceptions
    e_InsufficientFunds EXCEPTION;
    e_InvalidAmount EXCEPTION;
    e_AccountNotFound EXCEPTION;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- SafeTransferFunds Execution ---');
    
    -- Validate amount
    IF p_Amount <= 0 THEN
        RAISE e_InvalidAmount;
    END IF;

    -- Check if accounts exist
    SELECT COUNT(*) INTO v_SourceCount FROM Accounts WHERE AccountID = p_SourceAccountID;
    SELECT COUNT(*) INTO v_DestCount FROM Accounts WHERE AccountID = p_DestAccountID;
    
    IF v_SourceCount = 0 OR v_DestCount = 0 THEN
        RAISE e_AccountNotFound;
    END IF;

    -- Fetch source balance and lock the row
    SELECT Balance INTO v_SourceBalance 
    FROM Accounts 
    WHERE AccountID = p_SourceAccountID 
    FOR UPDATE;
    
    -- Check for sufficient funds
    IF v_SourceBalance < p_Amount THEN
        RAISE e_InsufficientFunds;
    END IF;

    -- Deduct from source account
    UPDATE Accounts 
    SET Balance = Balance - p_Amount, LastModified = SYSDATE 
    WHERE AccountID = p_SourceAccountID;
    
    -- Add to destination account
    UPDATE Accounts 
    SET Balance = Balance + p_Amount, LastModified = SYSDATE 
    WHERE AccountID = p_DestAccountID;

    -- Create transaction logs
    -- Fetch next transaction ID dynamically
    SELECT NVL(MAX(TransactionID), 0) + 1 INTO v_NextTxID FROM Transactions;
    INSERT INTO Transactions (TransactionID, AccountID, TransactionDate, Amount, TransactionType)
    VALUES (v_NextTxID, p_SourceAccountID, SYSDATE, p_Amount, 'Withdrawal');
    
    SELECT NVL(MAX(TransactionID), 0) + 1 INTO v_NextTxID FROM Transactions;
    INSERT INTO Transactions (TransactionID, AccountID, TransactionDate, Amount, TransactionType)
    VALUES (v_NextTxID, p_DestAccountID, SYSDATE, p_Amount, 'Deposit');

    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Success: Transferred $' || p_Amount || ' from Account ID ' || p_SourceAccountID || 
                         ' to Account ID ' || p_DestAccountID);
EXCEPTION
    WHEN e_InvalidAmount THEN
        ROLLBACK;
        INSERT INTO ErrorLogs (ProcedureName, ErrorMessage)
        VALUES ('SafeTransferFunds', 'Transfer amount must be positive. Amount: ' || p_Amount);
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Error: Transfer amount must be positive. Transaction rolled back.');
        
    WHEN e_AccountNotFound THEN
        ROLLBACK;
        INSERT INTO ErrorLogs (ProcedureName, ErrorMessage)
        VALUES ('SafeTransferFunds', 'Account not found. SourceID: ' || p_SourceAccountID || 
                ', DestID: ' || p_DestAccountID);
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Error: Source or Destination account not found. Transaction rolled back.');
        
    WHEN e_InsufficientFunds THEN
        ROLLBACK;
        INSERT INTO ErrorLogs (ProcedureName, ErrorMessage)
        VALUES ('SafeTransferFunds', 'Insufficient balance in Account ID ' || p_SourceAccountID || 
                '. Balance: $' || v_SourceBalance || ', Attempted: $' || p_Amount);
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Error: Insufficient funds in source account. Transaction rolled back.');
        
    WHEN OTHERS THEN
        ROLLBACK;
        INSERT INTO ErrorLogs (ProcedureName, ErrorMessage)
        VALUES ('SafeTransferFunds', 'Unexpected error: ' || SQLERRM);
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Error: Unexpected error occurred. Error logged in ErrorLogs table.');
END;
/

-- Scenario 2: UpdateSalary procedure with employee existence exception handling
CREATE OR REPLACE PROCEDURE UpdateSalary(
    p_EmployeeID IN NUMBER,
    p_Percentage IN NUMBER
) AS
    v_Count NUMBER;
    v_OldSalary NUMBER;
    v_NewSalary NUMBER;
    e_EmployeeNotFound EXCEPTION;
    e_InvalidPercentage EXCEPTION;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- UpdateSalary Execution ---');
    
    -- Check for negative percentage
    IF p_Percentage < 0 THEN
        RAISE e_InvalidPercentage;
    END IF;

    -- Check if employee exists
    SELECT COUNT(*) INTO v_Count FROM Employees WHERE EmployeeID = p_EmployeeID;
    IF v_Count = 0 THEN
        RAISE e_EmployeeNotFound;
    END IF;

    -- Fetch current salary
    SELECT Salary INTO v_OldSalary FROM Employees WHERE EmployeeID = p_EmployeeID;
    
    -- Calculate and update
    v_NewSalary := v_OldSalary * (1 + p_Percentage / 100);
    UPDATE Employees
    SET Salary = v_NewSalary
    WHERE EmployeeID = p_EmployeeID;
    
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Success: Salary for Employee ID ' || p_EmployeeID || ' increased by ' || p_Percentage || 
                         '%. Old: $' || v_OldSalary || ' | New: $' || v_NewSalary);
EXCEPTION
    WHEN e_InvalidPercentage THEN
        ROLLBACK;
        INSERT INTO ErrorLogs (ProcedureName, ErrorMessage)
        VALUES ('UpdateSalary', 'Salary increase percentage cannot be negative. Value: ' || p_Percentage);
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Error: Invalid percentage. Salary update aborted.');
        
    WHEN e_EmployeeNotFound THEN
        ROLLBACK;
        INSERT INTO ErrorLogs (ProcedureName, ErrorMessage)
        VALUES ('UpdateSalary', 'Employee ID not found: ' || p_EmployeeID);
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Error: Employee with ID ' || p_EmployeeID || ' does not exist.');
        
    WHEN OTHERS THEN
        ROLLBACK;
        INSERT INTO ErrorLogs (ProcedureName, ErrorMessage)
        VALUES ('UpdateSalary', 'Unexpected error: ' || SQLERRM);
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Error: Unexpected error during salary update.');
END;
/

-- Scenario 3: AddNewCustomer procedure handling primary key duplication
CREATE OR REPLACE PROCEDURE AddNewCustomer(
    p_CustomerID IN NUMBER,
    p_Name IN VARCHAR2,
    p_DOB IN DATE,
    p_Balance IN NUMBER
) AS
    e_InvalidBalance EXCEPTION;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- AddNewCustomer Execution ---');
    
    -- Balance sanity check
    IF p_Balance < 0 THEN
        RAISE e_InvalidBalance;
    END IF;
    
    -- Attempt insert
    INSERT INTO Customers (CustomerID, Name, DOB, Balance, LastModified)
    VALUES (p_CustomerID, p_Name, p_DOB, p_Balance, SYSDATE);
    
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Success: New customer ' || p_Name || ' (ID: ' || p_CustomerID || ') added successfully.');
EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        ROLLBACK;
        INSERT INTO ErrorLogs (ProcedureName, ErrorMessage)
        VALUES ('AddNewCustomer', 'Duplicate CustomerID: ' || p_CustomerID || ' for customer: ' || p_Name);
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Error: Customer ID ' || p_CustomerID || ' already exists. Insertion prevented.');
        
    WHEN e_InvalidBalance THEN
        ROLLBACK;
        INSERT INTO ErrorLogs (ProcedureName, ErrorMessage)
        VALUES ('AddNewCustomer', 'Customer initial balance cannot be negative. ID: ' || p_CustomerID || ', Balance: ' || p_Balance);
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Error: Initial balance cannot be negative.');
        
    WHEN OTHERS THEN
        ROLLBACK;
        INSERT INTO ErrorLogs (ProcedureName, ErrorMessage)
        VALUES ('AddNewCustomer', 'Unexpected error: ' || SQLERRM);
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Error: Unexpected error during customer insertion.');
END;
/
