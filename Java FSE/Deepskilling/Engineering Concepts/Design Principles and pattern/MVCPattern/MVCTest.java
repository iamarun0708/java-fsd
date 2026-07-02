package MVCPattern;

/**
 * Exercise 10: Implementing the MVC Pattern
 * 
 * Scenario: A simple web application for managing student records.
 */

// --- Model ---
class Student {
    private String name;
    private int id;
    private String grade;

    public Student(int id, String name, String grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
}

// --- View ---
class StudentView {
    public void displayStudentDetails(String name, int id, String grade) {
        System.out.println("  ┌─────────────────────────────┐");
        System.out.println("  │      Student Details         │");
        System.out.println("  ├─────────────────────────────┤");
        System.out.println("  │  ID    : " + padRight(String.valueOf(id), 18) + "│");
        System.out.println("  │  Name  : " + padRight(name, 18) + "│");
        System.out.println("  │  Grade : " + padRight(grade, 18) + "│");
        System.out.println("  └─────────────────────────────┘");
    }

    private String padRight(String s, int length) {
        return String.format("%-" + length + "s", s);
    }
}

// --- Controller ---
class StudentController {
    private Student model;
    private StudentView view;

    public StudentController(Student model, StudentView view) {
        this.model = model;
        this.view = view;
    }

    // Getters delegate to model
    public String getStudentName() { return model.getName(); }
    public int getStudentId() { return model.getId(); }
    public String getStudentGrade() { return model.getGrade(); }

    // Setters delegate to model
    public void setStudentName(String name) {
        model.setName(name);
    }

    public void setStudentGrade(String grade) {
        model.setGrade(grade);
    }

    // Update view from model
    public void updateView() {
        view.displayStudentDetails(model.getName(), model.getId(), model.getGrade());
    }
}

// --- Test Class ---
public class MVCTest {
    public static void main(String[] args) {
        System.out.println("=== MVC Pattern Demo ===\n");

        // Create model
        Student student = new Student(101, "Arun Kumar", "A");

        // Create view
        StudentView view = new StudentView();

        // Create controller
        StudentController controller = new StudentController(student, view);

        // Display initial student details
        System.out.println("--- Initial Student Record ---");
        controller.updateView();

        // Update student details via controller
        System.out.println("\n--- After Updating Name and Grade ---");
        controller.setStudentName("Arun Kumar S");
        controller.setStudentGrade("A+");
        controller.updateView();

        System.out.println("\n✓ MVC separates data (Model), UI (View), and logic (Controller)!");
    }
}
