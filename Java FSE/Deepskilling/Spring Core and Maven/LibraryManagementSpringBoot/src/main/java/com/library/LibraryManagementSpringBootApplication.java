package com.library;

import com.library.entity.Book;
import com.library.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LibraryManagementSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementSpringBootApplication.class, args);
        System.out.println("\n--- LibraryManagement Spring Boot Application started successfully! ---");
    }

    @Bean
    public CommandLineRunner demo(BookRepository repository) {
        return (args) -> {
            // Seed H2 DB with sample books
            repository.save(new Book("The Hobbit", "J.R.R. Tolkien"));
            repository.save(new Book("1984", "George Orwell"));
            repository.save(new Book("To Kill a Mockingbird", "Harper Lee"));
            System.out.println("--- Sample Book Data seeded into H2 Database ---");
        };
    }
}
