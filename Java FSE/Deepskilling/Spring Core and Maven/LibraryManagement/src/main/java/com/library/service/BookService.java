package com.library.service;

import com.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    private BookRepository bookRepository;

    // Default constructor for setter injection and fallback
    public BookService() {
    }

    // Constructor for Constructor Injection
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        System.out.println("BookService initialized via Constructor Injection.");
    }

    // Setter for Setter Injection
    @Autowired
    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        System.out.println("BookRepository injected into BookService via Setter Injection.");
    }

    public void addBook(String bookName) {
        System.out.println("BookService: Requesting repository to save book...");
        bookRepository.saveBook(bookName);
    }

    public void removeBook(String bookName) {
        System.out.println("BookService: Requesting repository to delete book...");
        bookRepository.deleteBook(bookName);
    }
}
