# Oracle PL/SQL Exercises & Schema Implementation

This directory contains the database schema definition, initial test data, and solution scripts for Exercises 1 to 7. All scripts are fully syntax-compliant with standard Oracle PL/SQL.

---

## Directory Structure

```
PL Sql/
├── schema.sql           - Table definitions (Customers, Accounts, Transactions, Loans, Employees, AuditLog, ErrorLogs) & Sample Data
├── exercise1.sql        - Exercise 1: Control Structures (Loan Interest Discount, VIP Promotion, Due Date Reminders)
├── exercise2.sql        - Exercise 2: Error Handling (SafeTransferFunds, UpdateSalary, AddNewCustomer)
├── exercise3.sql        - Exercise 3: Stored Procedures (ProcessMonthlyInterest, UpdateEmployeeBonus, TransferFunds)
├── exercise4.sql        - Exercise 4: Functions (CalculateAge, CalculateMonthlyInstallment, HasSufficientBalance)
├── exercise5.sql        - Exercise 5: Triggers (UpdateCustomerLastModified, LogTransaction, CheckTransactionRules)
├── exercise6.sql        - Exercise 6: Cursors (GenerateMonthlyStatements, ApplyAnnualFee, UpdateLoanInterestRates)
├── exercise7.sql        - Exercise 7: Packages (CustomerManagement, EmployeeManagement, AccountOperations)
└── README.md            - Complete documentation & execution guide
```

---

## How to Set Up and Run

### 1. Database Setup
Ensure you are connected to an Oracle Database instance (e.g. Oracle XE, Oracle Database Cloud, or Oracle Live SQL). Run [schema.sql](file:///c:/Users/arun/Documents/java-fsd/Java%20FSE/Deepskilling/PL%20Sql/schema.sql) first to set up all tables and seed sample data.

In SQL*Plus or SQL Developer:
```sql
@schema.sql
```

### 2. Enabling Output
For all scripts that output console messages (using `DBMS_OUTPUT.PUT_LINE`), make sure output is enabled in your session:
```sql
SET SERVEROUTPUT ON;
```

### 3. Running Individual Exercises
You can run any of the exercise scripts directly using:
```sql
@exercise1.sql
@exercise2.sql
-- ... and so on
```

---

## Exercise Details & Verification Queries

### Exercise 1: Control Structures
- **Scenario 1 (Discount):** Loops through customers and applies a 1% discount to loan interest rates for those over 60.
- **Scenario 2 (VIP):** Iterates through customers and promotes those with a balance over $10,000 to VIP status.
- **Scenario 3 (Reminders):** Fetches and prints reminder alerts for loans ending within the next 30 days.

*Verification Query:*
```sql
SELECT CustomerID, Name, IsVIP FROM Customers;
SELECT LoanID, CustomerID, InterestRate, EndDate FROM Loans;
```

### Exercise 2: Error Handling
- **`SafeTransferFunds`:** Stored procedure that safely transfers money between accounts, handles insufficient funds, and logs any transaction errors to `ErrorLogs`.
- **`UpdateSalary`:** Stored procedure that increases an employee's salary by a given percentage. Logs error if employee ID doesn't exist.
- **`AddNewCustomer`:** Stored procedure that inserts a new customer and handles duplication checks.

*Verification Queries:*
```sql
-- Test duplicate customer insertion
EXEC AddNewCustomer(1, 'Duplicate John', TO_DATE('1985-05-15', 'YYYY-MM-DD'), 1000);

-- Test invalid fund transfer
EXEC SafeTransferFunds(1, 2, 5000); -- More than balance

-- Inspect log
SELECT * FROM ErrorLogs;
```

### Exercise 3: Stored Procedures
- **`ProcessMonthlyInterest`:** Applies a 1% interest rate to all savings accounts.
- **`UpdateEmployeeBonus`:** Updates salary of employees in a department by adding a percentage bonus.
- **`TransferFunds`:** Standard fund transfer with explicit balance checks.

*Verification Queries:*
```sql
-- Check balance before interest
SELECT AccountID, Balance, AccountType FROM Accounts;
EXEC ProcessMonthlyInterest;
-- Check balance after interest
SELECT AccountID, Balance, AccountType FROM Accounts;
```

### Exercise 4: Functions
- **`CalculateAge(p_DOB)`:** Takes DOB and returns age in years.
- **`CalculateMonthlyInstallment(p_LoanAmount, p_InterestRate, p_DurationYears)`:** Computes standard monthly EMI.
- **`HasSufficientBalance(p_AccountID, p_Amount)`:** Returns BOOLEAN (TRUE/FALSE) if account has at least the specified amount.

*Verification Queries:*
```sql
SELECT CalculateAge(DOB) AS Age, Name FROM Customers;
SELECT CalculateMonthlyInstallment(10000, 6, 3) FROM DUAL;
```

### Exercise 5: Triggers
- **`UpdateCustomerLastModified`:** Trigger updating `LastModified` whenever a customer record is updated.
- **`LogTransaction`:** Audits all inserts on `Transactions` to the `AuditLog` table.
- **`CheckTransactionRules`:** Enforces withdrawal limit (<= balance) and deposit positivity (must be > 0).

*Verification Queries:*
```sql
-- Test triggers
UPDATE Customers SET Name = 'Jane Smith Jr.' WHERE CustomerID = 2;
SELECT LastModified FROM Customers WHERE CustomerID = 2; -- Should be updated to SYSDATE

INSERT INTO Transactions (TransactionID, AccountID, TransactionDate, Amount, TransactionType)
VALUES (3, 2, SYSDATE, -100, 'Deposit'); -- Should fail with ORA-20001
```

### Exercise 6: Cursors
- **`GenerateMonthlyStatements`:** Cursor that prints all transactions for the current month.
- **`ApplyAnnualFee`:** Cursor deducting an annual maintenance fee of $20.00 from all accounts.
- **`UpdateLoanInterestRates`:** Cursor that updates loan rates (5.5% for > $5,000; 6.5% otherwise).

*Verification Queries:*
```sql
SELECT AccountID, Balance FROM Accounts;
-- Run Exercise 6 block
SELECT AccountID, Balance FROM Accounts;
```

### Exercise 7: Packages
- **`CustomerManagement`:** Procedures to add/update customers, and a function to get balance.
- **`EmployeeManagement`:** Procedures to hire/update employees, and a function to get annual salary.
- **`AccountOperations`:** Procedures to open/close accounts, and a function to get total balance across all accounts.

*Verification Queries:*
```sql
-- Use Package methods
EXEC CustomerManagement.AddCustomer(4, 'David Lee', TO_DATE('1992-04-12', 'YYYY-MM-DD'), 2500);
SELECT CustomerManagement.GetCustomerBalance(4) FROM DUAL;
SELECT AccountOperations.GetTotalBalance(1) FROM DUAL;
```
