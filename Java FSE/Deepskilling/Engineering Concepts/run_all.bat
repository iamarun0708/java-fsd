@echo off
REM ============================================================
REM  Master Run Script - Design Patterns & Algorithms in Java
REM  Usage: run_all.bat [exercise_number]
REM    No argument  = compile and run ALL exercises
REM    With number  = run specific exercise (1-18)
REM ============================================================

set BASE=c:\Users\arun\Documents\java-fsd\Java FSE\Deepskilling\Engineering Concepts
set DP=%BASE%\Design Principles and pattern
set ALGO=%BASE%\Algorithms and DataStructures

if "%1"=="" goto all
goto exercise_%1

:all
echo.
echo ************************************************************
echo *        RUNNING ALL EXERCISES - DESIGN PATTERNS            *
echo ************************************************************

:exercise_1
echo.
echo ============================================================
echo  Exercise 1: Singleton Pattern
echo ============================================================
javac "%DP%\SingletonPattern\Logger.java" "%DP%\SingletonPattern\SingletonTest.java"
java -cp "%DP%" SingletonPattern.SingletonTest
if not "%1"=="" goto done

:exercise_2
echo.
echo ============================================================
echo  Exercise 2: Factory Method Pattern
echo ============================================================
javac "%DP%\FactoryMethodPattern\FactoryMethodTest.java"
java -cp "%DP%" FactoryMethodPattern.FactoryMethodTest
if not "%1"=="" goto done

:exercise_3
echo.
echo ============================================================
echo  Exercise 3: Builder Pattern
echo ============================================================
javac "%DP%\BuilderPattern\BuilderTest.java"
java -cp "%DP%" BuilderPattern.BuilderTest
if not "%1"=="" goto done

:exercise_4
echo.
echo ============================================================
echo  Exercise 4: Adapter Pattern
echo ============================================================
javac "%DP%\AdapterPattern\AdapterTest.java"
java -cp "%DP%" AdapterPattern.AdapterTest
if not "%1"=="" goto done

:exercise_5
echo.
echo ============================================================
echo  Exercise 5: Decorator Pattern
echo ============================================================
javac "%DP%\DecoratorPattern\DecoratorTest.java"
java -cp "%DP%" DecoratorPattern.DecoratorTest
if not "%1"=="" goto done

:exercise_6
echo.
echo ============================================================
echo  Exercise 6: Proxy Pattern
echo ============================================================
javac "%DP%\ProxyPattern\ProxyTest.java"
java -cp "%DP%" ProxyPattern.ProxyTest
if not "%1"=="" goto done

:exercise_7
echo.
echo ============================================================
echo  Exercise 7: Observer Pattern
echo ============================================================
javac "%DP%\ObserverPattern\ObserverTest.java"
java -cp "%DP%" ObserverPattern.ObserverTest
if not "%1"=="" goto done

:exercise_8
echo.
echo ============================================================
echo  Exercise 8: Strategy Pattern
echo ============================================================
javac "%DP%\StrategyPattern\StrategyTest.java"
java -cp "%DP%" StrategyPattern.StrategyTest
if not "%1"=="" goto done

:exercise_9
echo.
echo ============================================================
echo  Exercise 9: Command Pattern
echo ============================================================
javac "%DP%\CommandPattern\CommandTest.java"
java -cp "%DP%" CommandPattern.CommandTest
if not "%1"=="" goto done

:exercise_10
echo.
echo ============================================================
echo  Exercise 10: MVC Pattern
echo ============================================================
javac "%DP%\MVCPattern\MVCTest.java"
java -cp "%DP%" MVCPattern.MVCTest
if not "%1"=="" goto done

:exercise_11
echo.
echo ============================================================
echo  Exercise 11: Dependency Injection
echo ============================================================
javac "%DP%\DependencyInjection\DependencyInjectionTest.java"
java -cp "%DP%" DependencyInjection.DependencyInjectionTest
if not "%1"=="" goto done

echo.
echo ************************************************************
echo *      RUNNING ALL EXERCISES - ALGORITHMS ^& DATA STRUCTURES *
echo ************************************************************

:exercise_12
echo.
echo ============================================================
echo  Exercise 12: Inventory Management (ArrayList vs HashMap)
echo ============================================================
javac "%ALGO%\InventoryManagement\InventoryTest.java"
java -cp "%ALGO%" InventoryManagement.InventoryTest
if not "%1"=="" goto done

:exercise_13
echo.
echo ============================================================
echo  Exercise 13: E-commerce Search (Linear vs Binary)
echo ============================================================
javac "%ALGO%\EcommerceSearch\SearchTest.java"
java -cp "%ALGO%" EcommerceSearch.SearchTest
if not "%1"=="" goto done

:exercise_14
echo.
echo ============================================================
echo  Exercise 14: Sorting Orders (Bubble vs Quick Sort)
echo ============================================================
javac "%ALGO%\SortingOrders\SortingTest.java"
java -cp "%ALGO%" SortingOrders.SortingTest
if not "%1"=="" goto done

:exercise_15
echo.
echo ============================================================
echo  Exercise 15: Employee Management (Arrays)
echo ============================================================
javac "%ALGO%\EmployeeManagement\EmployeeTest.java"
java -cp "%ALGO%" EmployeeManagement.EmployeeTest
if not "%1"=="" goto done

:exercise_16
echo.
echo ============================================================
echo  Exercise 16: Task Management (Linked List)
echo ============================================================
javac "%ALGO%\TaskManagement\TaskManagementTest.java"
java -cp "%ALGO%" TaskManagement.TaskManagementTest
if not "%1"=="" goto done

:exercise_17
echo.
echo ============================================================
echo  Exercise 17: Library Management (Search)
echo ============================================================
javac "%ALGO%\LibraryManagement\LibraryTest.java"
java -cp "%ALGO%" LibraryManagement.LibraryTest
if not "%1"=="" goto done

:exercise_18
echo.
echo ============================================================
echo  Exercise 18: Financial Forecasting (Recursion)
echo ============================================================
javac "%ALGO%\FinancialForecasting\FinancialForecastTest.java"
java -cp "%ALGO%" FinancialForecasting.FinancialForecastTest
if not "%1"=="" goto done

echo.
echo ************************************************************
echo *               ALL 18 EXERCISES COMPLETED!                 *
echo ************************************************************

:done
