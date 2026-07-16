package com.cognizant.springlearn.controller;

import com.cognizant.springlearn.model.Country;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CountryController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CountryController.class);

    private final List<Country> countriesList;

    public CountryController() {
        countriesList = new ArrayList<>();
        countriesList.add(new Country("IN", "India"));
        countriesList.add(new Country("US", "United States of America"));
        countriesList.add(new Country("DE", "Germany"));
        countriesList.add(new Country("JP", "Japan"));
        countriesList.add(new Country("FR", "France"));
    }

    @GetMapping("/country")
    public Country getCountryIndia() {
        LOGGER.info("START: getCountryIndia()");
        Country india = new Country("IN", "India");
        LOGGER.info("END: getCountryIndia()");
        return india;
    }

    @GetMapping("/countries")
    public List<Country> getAllCountries() {
        LOGGER.info("START: getAllCountries()");
        LOGGER.info("END: getAllCountries()");
        return countriesList;
    }

    @GetMapping("/countries/{code}")
    public ResponseEntity<Country> getCountryByCode(@PathVariable String code) {
        LOGGER.info("START: getCountryByCode() with code: {}", code);
        for (Country country : countriesList) {
            if (country.getCode().equalsIgnoreCase(code)) {
                LOGGER.info("END: getCountryByCode() found: {}", country);
                return ResponseEntity.ok(country);
            }
        }
        LOGGER.warn("END: getCountryByCode() country not found for code: {}", code);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
