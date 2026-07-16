package com.library.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class BookRepository {
    public void saveBook(String bookName) {
        System.out.println("Saving book to repository: " + bookName);
    }

    public void deleteBook(String bookName) {
        System.out.println("Deleting book from repository: " + bookName);
    }
}
