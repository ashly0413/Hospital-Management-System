package application;

import java.util.Scanner;

public class Staff {

    private String id;
    private String name;
    private String designation;
    private String sex;
    private int salary;

    // Constructor
    public Staff(String id, String name, String designation,
                 String sex, int salary) {
        this.id = id;
        this.name = name;
        this.designation = designation;
        this.sex = sex;
        this.salary = salary;
    }

 // Add new staff
    public void newStaff() {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter Staff ID: ");
        id = input.nextLine();

        System.out.print("Enter Staff Name: ");
        name = input.nextLine();

        System.out.print("Enter Designation: ");
        designation = input.nextLine();

        System.out.print("Enter Sex: ");
        sex = input.nextLine();

        System.out.print("Enter Salary: ");
        salary = input.nextInt();
    }

 // Show information
    public String showStaffInfo() {
        return id + " " + name + " " + designation + " " + sex + " " + salary;
    }

    // Getter
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDesignation() { return designation; }
    public String getSex() { return sex; }
    public int getSalary() { return salary; }
}
