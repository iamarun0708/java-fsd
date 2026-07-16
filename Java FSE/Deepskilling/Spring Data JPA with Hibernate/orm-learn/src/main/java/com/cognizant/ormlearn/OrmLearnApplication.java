package com.cognizant.ormlearn;

import com.cognizant.ormlearn.model.*;
import com.cognizant.ormlearn.service.AttemptService;
import com.cognizant.ormlearn.service.CountryService;
import com.cognizant.ormlearn.service.EmployeeService;
import com.cognizant.ormlearn.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class OrmLearnApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrmLearnApplication.class);

    private static CountryService countryService;
    private static StockService stockService;
    private static EmployeeService employeeService;
    private static AttemptService attemptService;

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(OrmLearnApplication.class, args);
        LOGGER.info("OrmLearnApplication started successfully!");

        countryService = context.getBean(CountryService.class);
        stockService = context.getBean(StockService.class);
        employeeService = context.getBean(EmployeeService.class);
        attemptService = context.getBean(AttemptService.class);

        try {
            System.out.println("\n==================================================");
            System.out.println("TESTING SPRING DATA JPA HANDS-ON");
            System.out.println("==================================================");

            // 1. Country Service Tests
            testGetAllCountries();
            testCountryCrud();
            testCountrySearches();

            // 2. Stock Service Tests
            testStockQueries();

            // 3. Employee Service Tests
            testEmployeeQueries();

            // 4. Quiz Attempt HQL Tests
            testQuizAttemptDetails();

        } catch (Exception e) {
            LOGGER.error("Error occurred during tests", e);
        }
    }

    private static void testGetAllCountries() {
        LOGGER.info("Start: testGetAllCountries");
        List<Country> countries = countryService.getAllCountries();
        LOGGER.info("All Countries: {}", countries);
        LOGGER.info("End: testGetAllCountries");
    }

    private static void testCountryCrud() throws Exception {
        LOGGER.info("Start: testCountryCrud");
        // Add
        Country newCountry = new Country("ZZ", "Z-Test Land");
        countryService.addCountry(newCountry);
        LOGGER.info("Added: {}", countryService.findCountryByCode("ZZ"));

        // Update
        countryService.updateCountry("ZZ", "Z-Test Land Updated");
        LOGGER.info("Updated: {}", countryService.findCountryByCode("ZZ"));

        // Delete
        countryService.deleteCountry("ZZ");
        try {
            countryService.findCountryByCode("ZZ");
            LOGGER.error("ZZ was not deleted!");
        } catch (Exception e) {
            LOGGER.info("ZZ successfully deleted: " + e.getMessage());
        }
        LOGGER.info("End: testCountryCrud");
    }

    private static void testCountrySearches() {
        LOGGER.info("Start: testCountrySearches");
        // Search containing 'ou' in alphabetical order
        List<Country> search1 = countryService.searchCountriesByNameAsc("ou");
        LOGGER.info("Countries containing 'ou' (sorted):");
        search1.forEach(c -> LOGGER.info(" - {} : {}", c.getCode(), c.getName()));

        // Search starting with 'Z'
        List<Country> search2 = countryService.searchCountriesStartingWith("Z");
        LOGGER.info("Countries starting with 'Z':");
        search2.forEach(c -> LOGGER.info(" - {} : {}", c.getCode(), c.getName()));
        LOGGER.info("End: testCountrySearches");
    }

    private static void testStockQueries() throws Exception {
        LOGGER.info("Start: testStockQueries");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date start = sdf.parse("2019-09-01");
        Date end = sdf.parse("2019-09-30");

        // FB Sept 2019
        List<Stock> fbStocks = stockService.getFacebookStocksInSept2019(start, end);
        LOGGER.info("Facebook Stocks in September 2019:");
        fbStocks.forEach(s -> LOGGER.info(" - Date: {}, Open: {}, Close: {}, Volume: {}",
                sdf.format(s.getDate()), s.getOpen(), s.getClose(), s.getVolume()));

        // Google > 1250
        List<Stock> googleStocks = stockService.getGoogleStocksGreaterThan1250();
        LOGGER.info("Google Stocks with close price > 1250:");
        googleStocks.forEach(s -> LOGGER.info(" - Date: {}, Open: {}, Close: {}, Volume: {}",
                sdf.format(s.getDate()), s.getOpen(), s.getClose(), s.getVolume()));

        // Top 3 highest volume
        List<Stock> top3 = stockService.getTop3HighestVolumeStocks();
        LOGGER.info("Top 3 Highest Volume Stocks:");
        top3.forEach(s -> LOGGER.info(" - Code: {}, Date: {}, Volume: {}",
                s.getCode(), sdf.format(s.getDate()), s.getVolume()));
        LOGGER.info("End: testStockQueries");
    }

    private static void testEmployeeQueries() {
        LOGGER.info("Start: testEmployeeQueries");
        // Permanent employees (HQL left join fetch)
        List<Employee> permanent = employeeService.getAllPermanentEmployees();
        LOGGER.info("Permanent Employees:");
        permanent.forEach(e -> {
            LOGGER.info(" - Employee: {}, Department: {}", e.getName(), e.getDepartment().getName());
            LOGGER.info("   Skills: {}", e.getSkillList());
        });

        // Avg Salary IT department (id = 1)
        double avgSalaryIt = employeeService.getAverageSalary(1);
        LOGGER.info("Average Salary in IT Department (ID=1): {}", avgSalaryIt);

        // Native query check
        List<Employee> allNative = employeeService.getAllEmployeesNative();
        LOGGER.info("All Employees (Native Query):");
        allNative.forEach(e -> LOGGER.info(" - Name: {}, Salary: {}", e.getName(), e.getSalary()));

        // Criteria Query (Dynamic filter)
        List<Employee> filtered = employeeService.getEmployeesByCriteria("John", new BigDecimal("5000.00"));
        LOGGER.info("Employees matching criteria (name='John', salary > 5000):");
        filtered.forEach(e -> LOGGER.info(" - Match found: {}", e.getName()));
        LOGGER.info("End: testEmployeeQueries");
    }

    private static void testQuizAttemptDetails() {
        LOGGER.info("Start: testQuizAttemptDetails");
        // Get Arun's attempt (UserId=1, AttemptId=1)
        Attempt attempt = attemptService.getAttempt(1, 1);
        if (attempt != null) {
            System.out.println("\n--- QUIZ ATTEMPT REPORT ---");
            System.out.println("User: " + attempt.getUser().getName());
            System.out.println("Date: " + attempt.getDate());
            System.out.println("Total Attempt Score: " + attempt.getScore());
            System.out.println("----------------------------------------");

            for (AttemptQuestion aq : attempt.getAttemptQuestions()) {
                Question q = aq.getQuestion();
                System.out.println(q.getText());
                
                // Get selected option
                Integer selectedOpId = -1;
                if (aq.getAttemptOptions() != null && !aq.getAttemptOptions().isEmpty()) {
                    selectedOpId = aq.getAttemptOptions().get(0).getOptions().getId();
                }

                int optionIndex = 1;
                for (Options o : q.getOptionsList()) {
                    double scoreVal = o.getCorrect() ? q.getScore().doubleValue() : 0.0;
                    boolean isSelected = o.getId().equals(selectedOpId);
                    System.out.printf("  %d) %-12s %-6.1f %-6b\n", optionIndex++, o.getText(), scoreVal, isSelected);
                }
            }
            System.out.println("----------------------------------------");
        } else {
            LOGGER.error("Quiz Attempt not found!");
        }
        LOGGER.info("End: testQuizAttemptDetails");
    }
}
