package com.library;

import com.library.service.BookService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LibraryManagementApplication {
    public static void main(String[] args) {
        System.out.println("--- Loading Spring XML Application Context ---");
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        System.out.println("--- Context Loaded Successfully ---");

        // 1. Test XML-configured constructor injected bean (xmlBookService)
        System.out.println("\nTesting XML Configured Bean (xmlBookService):");
        BookService xmlService = (BookService) context.getBean("xmlBookService");
        xmlService.addBook("Spring in Action");

        // 2. Test Annotation-scanned setter injected bean (bookService)
        System.out.println("\nTesting Annotation Scanned Bean (bookService):");
        BookService annoService = (BookService) context.getBean("bookService");
        annoService.addBook("Design Patterns Elements of Reusable Object-Oriented Software");
        annoService.removeBook("Clean Code");
        
        System.out.println("\n--- LibraryManagementApplication execution finished ---");
    }
}
