-- SET SERVEROUTPUT ON for Oracle SQL Developer / Live SQL execution
SET SERVEROUTPUT ON;

-- ==========================================
-- Exercise 5: Triggers
-- ==========================================

-- Scenario 1: Automatically update the LastModified date when a customer's record is updated
CREATE OR REPLACE TRIGGER UpdateCustomerLastModified
BEFORE UPDATE ON Customers
FOR EACH ROW
BEGIN
    :new.LastModified := SYSDATE;
END;
/

-- Scenario 2: Maintain an audit log for all transactions
CREATE OR REPLACE TRIGGER LogTransaction
AFTER INSERT ON Transactions
FOR EACH ROW
BEGIN
    INSERT INTO AuditLog (
        TransactionID, 
        AccountID, 
        TransactionDate, 
        Amount, 
        TransactionType, 
        LogDate
    )
    VALUES (
        :new.TransactionID, 
        :new.AccountID, 
        :new.TransactionDate, 
        :new.Amount, 
        :new.TransactionType, 
        SYSDATE
    );
END;
/

-- Scenario 3: Enforce business rules on deposits and withdrawals
CREATE OR REPLACE TRIGGER CheckTransactionRules
BEFORE INSERT ON Transactions
FOR EACH ROW
DECLARE
    v_Balance NUMBER;
BEGIN
    -- Deposit Amount Rule
    IF :new.TransactionType = 'Deposit' AND :new.Amount <= 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Deposit amount must be positive.');
    END IF;

    -- Withdrawal Rule
    IF :new.TransactionType = 'Withdrawal' THEN
        IF :new.Amount <= 0 THEN
            RAISE_APPLICATION_ERROR(-20002, 'Withdrawal amount must be positive.');
        END IF;

        -- Check if account has sufficient funds
        SELECT Balance INTO v_Balance 
        FROM Accounts 
        WHERE AccountID = :new.AccountID;

        IF v_Balance < :new.Amount THEN
            RAISE_APPLICATION_ERROR(-20003, 'Insufficient funds. Account balance is $' || v_Balance || 
                                    ', but withdrawal request is $' || :new.Amount);
        END IF;
    END IF;
END;
/
