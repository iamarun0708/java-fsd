package FactoryMethodPattern;

/**
 * Exercise 2: Implementing the Factory Method Pattern
 * 
 * Scenario: A document management system that creates different
 * types of documents (Word, PDF, Excel).
 */

// --- Abstract Product ---
abstract class Document {
    public abstract void open();
    public abstract void save();
    public abstract String getType();
}

// --- Concrete Products ---
class WordDocument extends Document {
    @Override
    public void open() {
        System.out.println("Opening Word Document...");
    }

    @Override
    public void save() {
        System.out.println("Saving Word Document as .docx");
    }

    @Override
    public String getType() {
        return "Word Document";
    }
}

class PdfDocument extends Document {
    @Override
    public void open() {
        System.out.println("Opening PDF Document...");
    }

    @Override
    public void save() {
        System.out.println("Saving PDF Document as .pdf");
    }

    @Override
    public String getType() {
        return "PDF Document";
    }
}

class ExcelDocument extends Document {
    @Override
    public void open() {
        System.out.println("Opening Excel Document...");
    }

    @Override
    public void save() {
        System.out.println("Saving Excel Document as .xlsx");
    }

    @Override
    public String getType() {
        return "Excel Document";
    }
}

// --- Abstract Factory ---
abstract class DocumentFactory {
    public abstract Document createDocument();
}

// --- Concrete Factories ---
class WordDocumentFactory extends DocumentFactory {
    @Override
    public Document createDocument() {
        return new WordDocument();
    }
}

class PdfDocumentFactory extends DocumentFactory {
    @Override
    public Document createDocument() {
        return new PdfDocument();
    }
}

class ExcelDocumentFactory extends DocumentFactory {
    @Override
    public Document createDocument() {
        return new ExcelDocument();
    }
}

// --- Test Class ---
public class FactoryMethodTest {
    public static void main(String[] args) {
        System.out.println("=== Factory Method Pattern Demo ===\n");

        // Create different documents using factories
        DocumentFactory wordFactory = new WordDocumentFactory();
        DocumentFactory pdfFactory = new PdfDocumentFactory();
        DocumentFactory excelFactory = new ExcelDocumentFactory();

        Document doc1 = wordFactory.createDocument();
        Document doc2 = pdfFactory.createDocument();
        Document doc3 = excelFactory.createDocument();

        // Use the documents
        Document[] documents = {doc1, doc2, doc3};
        for (Document doc : documents) {
            System.out.println("Type: " + doc.getType());
            doc.open();
            doc.save();
            System.out.println();
        }
    }
}
