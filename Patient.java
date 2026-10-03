package application;

import java.util.Scanner;

public class Patient {

    private String id;
    private String name;
    private String disease;
    private String sex;
    private String admitStatus;
    private int age;

    public Patient(String id, String name, String disease,
                   String sex, String admitStatus, int age) {
        this.id = id;
        this.name = name;
        this.disease = disease;
        this.sex = sex;
        this.admitStatus = admitStatus;
        this.age = age;
    }

    public void newPatient() {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter Patient ID: ");
        id = input.nextLine();

        System.out.print("Enter Patient Name: ");
        name = input.nextLine();

        System.out.print("Enter Disease: ");
        disease = input.nextLine();

        System.out.print("Enter Sex: ");
        sex = input.nextLine();

        System.out.print("Enter Admit Status: ");
        admitStatus = input.nextLine();

        System.out.print("Enter Age: ");
        age = input.nextInt();
    }

    public String showPatientInfo() {
        return id + " " + name + " " + disease + " " +
               sex + " " + admitStatus + " " + age;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDisease() { return disease; }
    public String getSex() { return sex; }
    public String getAdmitStatus() { return admitStatus; }
    public int getAge() { return age; }
}
