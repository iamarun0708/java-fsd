-- SET SERVEROUTPUT ON for Oracle SQL Developer / Live SQL execution
SET SERVEROUTPUT ON;

-- ==========================================
-- Exercise 7: Packages
-- ==========================================

-- -------------------------------------------------------------
-- Package 1: CustomerManagement
-- -------------------------------------------------------------
CREATE OR REPLACE PACKAGE CustomerManagement AS
    PROCEDURE AddCustomer(
        p_CustomerID IN NUMBER,
        p_Name IN VARCHAR2,
        p_DOB IN DATE,
        p_Balance IN NUMBER
    );
    
    PROCEDURE UpdateCustomer(
        p_CustomerID IN NUMBER,
        p_Name IN VARCHAR2,
        p_Balance IN NUMBER
    );
    
    FUNCTION GetCustomerBalance(
        p_CustomerID IN NUMBER
    ) RETURN NUMBER;
END CustomerManagement;
/

CREATE OR REPLACE PACKAGE BODY CustomerManagement AS
    PROCEDURE AddCustomer(
        p_CustomerID IN NUMBER,
        p_Name IN VARCHAR2,
        p_DOB IN DATE,
        p_Balance IN NUMBER
    ) IS
    BEGIN
        INSERT INTO Customers (CustomerID, Name, DOB, Balance, LastModified)
        VALUES (p_CustomerID, p_Name, p_DOB, p_Balance, SYSDATE);
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Success: Customer ' || p_Name || ' added.');
    EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN
            DBMS_OUTPUT.PUT_LINE('Error: Customer ID ' || p_CustomerID || ' already exists.');
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Error adding customer: ' || SQLERRM);
    END AddCustomer;

    PROCEDURE UpdateCustomer(
        p_CustomerID IN NUMBER,
        p_Name IN VARCHAR2,
        p_Balance IN NUMBER
    ) IS
    BEGIN
        UPDATE Customers
        SET Name = p_Name,
            Balance = p_Balance,
            LastModified = SYSDATE
        WHERE CustomerID = p_CustomerID;
        
        IF SQL%ROWCOUNT = 0 THEN
            DBMS_OUTPUT.PUT_LINE('Warning: Customer ID ' || p_CustomerID || ' not found.');
        ELSE
            COMMIT;
            DBMS_OUTPUT.PUT_LINE('Success: Customer ID ' || p_CustomerID || ' updated.');
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Error updating customer: ' || SQLERRM);
    END UpdateCustomer;

    FUNCTION GetCustomerBalance(
        p_CustomerID IN NUMBER
    ) RETURN NUMBER IS
        v_Balance NUMBER;
    BEGIN
        SELECT Balance INTO v_Balance 
        FROM Customers 
        WHERE CustomerID = p_CustomerID;
        
        RETURN v_Balance;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN NULL;
        WHEN OTHERS THEN
            RETURN NULL;
    END GetCustomerBalance;
END CustomerManagement;
/


-- -------------------------------------------------------------
-- Package 2: EmployeeManagement
-- -------------------------------------------------------------
CREATE OR REPLACE PACKAGE EmployeeManagement AS
    PROCEDURE HireEmployee(
        p_EmployeeID IN NUMBER,
        p_Name IN VARCHAR2,
        p_Position IN VARCHAR2,
        p_Salary IN NUMBER,
        p_Department IN VARCHAR2,
        p_HireDate IN DATE
    );
    
    PROCEDURE UpdateEmployee(
        p_EmployeeID IN NUMBER,
        p_Name IN VARCHAR2,
        p_Position IN VARCHAR2,
        p_Salary IN NUMBER,
        p_Department IN VARCHAR2
    );
    
    FUNCTION GetAnnualSalary(
        p_EmployeeID IN NUMBER
    ) RETURN NUMBER;
END EmployeeManagement;
/

CREATE OR REPLACE PACKAGE BODY EmployeeManagement AS
    PROCEDURE HireEmployee(
        p_EmployeeID IN NUMBER,
        p_Name IN VARCHAR2,
        p_Position IN VARCHAR2,
        p_Salary IN NUMBER,
        p_Department IN VARCHAR2,
        p_HireDate IN DATE
    ) IS
    BEGIN
        INSERT INTO Employees (EmployeeID, Name, Position, Salary, Department, HireDate)
        VALUES (p_EmployeeID, p_Name, p_Position, p_Salary, p_Department, p_HireDate);
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Success: Employee ' || p_Name || ' hired.');
    EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN
            DBMS_OUTPUT.PUT_LINE('Error: Employee ID ' || p_EmployeeID || ' already exists.');
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Error hiring employee: ' || SQLERRM);
    END HireEmployee;

    PROCEDURE UpdateEmployee(
        p_EmployeeID IN NUMBER,
        p_Name IN VARCHAR2,
        p_Position IN VARCHAR2,
        p_Salary IN NUMBER,
        p_Department IN VARCHAR2
    ) IS
    BEGIN
        UPDATE Employees
        SET Name = p_Name,
            Position = p_Position,
            Salary = p_Salary,
            Department = p_Department
        WHERE EmployeeID = p_EmployeeID;
        
        IF SQL%ROWCOUNT = 0 THEN
            DBMS_OUTPUT.PUT_LINE('Warning: Employee ID ' || p_EmployeeID || ' not found.');
        ELSE
            COMMIT;
            DBMS_OUTPUT.PUT_LINE('Success: Employee ID ' || p_EmployeeID || ' updated.');
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Error updating employee: ' || SQLERRM);
    END UpdateEmployee;

    FUNCTION GetAnnualSalary(
        p_EmployeeID IN NUMBER
    ) RETURN NUMBER IS
        v_MonthlySalary NUMBER;
    BEGIN
        SELECT Salary INTO v_MonthlySalary 
        FROM Employees 
        WHERE EmployeeID = p_EmployeeID;
        
        RETURN v_MonthlySalary * 12;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN NULL;
        WHEN OTHERS THEN
            RETURN NULL;
    END GetAnnualSalary;
END EmployeeManagement;
/


-- -------------------------------------------------------------
-- Package 3: AccountOperations
-- -------------------------------------------------------------
CREATE OR REPLACE PACKAGE AccountOperations AS
    PROCEDURE OpenAccount(
        p_AccountID IN NUMBER,
        p_CustomerID IN NUMBER,
        p_AccountType IN VARCHAR2,
        p_InitialBalance IN NUMBER
    );
    
    PROCEDURE CloseAccount(
        p_AccountID IN NUMBER
    );
    
    FUNCTION GetTotalBalance(
        p_CustomerID IN NUMBER
    ) RETURN NUMBER;
END AccountOperations;
/

CREATE OR REPLACE PACKAGE BODY AccountOperations AS
    PROCEDURE OpenAccount(
        p_AccountID IN NUMBER,
        p_CustomerID IN NUMBER,
        p_AccountType IN VARCHAR2,
        p_InitialBalance IN NUMBER
    ) IS
        v_CustCount NUMBER;
    BEGIN
        -- Check if Customer exists
        SELECT COUNT(*) INTO v_CustCount FROM Customers WHERE CustomerID = p_CustomerID;
        IF v_CustCount = 0 THEN
            DBMS_OUTPUT.PUT_LINE('Error: Cannot open account. Customer ID ' || p_CustomerID || ' does not exist.');
            RETURN;
        END IF;

        INSERT INTO Accounts (AccountID, CustomerID, AccountType, Balance, LastModified)
        VALUES (p_AccountID, p_CustomerID, p_AccountType, p_InitialBalance, SYSDATE);
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Success: Account ' || p_AccountID || ' opened for Customer ' || p_CustomerID);
    EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN
            DBMS_OUTPUT.PUT_LINE('Error: Account ID ' || p_AccountID || ' already exists.');
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Error opening account: ' || SQLERRM);
    END OpenAccount;

    PROCEDURE CloseAccount(
        p_AccountID IN NUMBER
    ) IS
    BEGIN
        DELETE FROM Accounts 
        WHERE AccountID = p_AccountID;
        
        IF SQL%ROWCOUNT = 0 THEN
            DBMS_OUTPUT.PUT_LINE('Warning: Account ID ' || p_AccountID || ' not found.');
        ELSE
            COMMIT;
            DBMS_OUTPUT.PUT_LINE('Success: Account ID ' || p_AccountID || ' closed.');
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Error closing account: ' || SQLERRM);
    END CloseAccount;

    FUNCTION GetTotalBalance(
        p_CustomerID IN NUMBER
    ) RETURN NUMBER IS
        v_TotalBalance NUMBER;
    BEGIN
        SELECT SUM(Balance) INTO v_TotalBalance 
        FROM Accounts 
        WHERE CustomerID = p_CustomerID;
        
        RETURN NVL(v_TotalBalance, 0);
    EXCEPTION
        WHEN OTHERS THEN
            RETURN 0;
    END GetTotalBalance;
END AccountOperations;
/
