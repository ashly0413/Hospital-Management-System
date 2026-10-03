package application;

import java.util.Scanner;

public class Doctor {

    private String id;
    private String name;
    private String specialist;
    private String workTime;
    private String qualification;
    private int room;

    public Doctor(String id, String name, String specialist,
                  String workTime, String qualification, int room) {
        this.id = id;
        this.name = name;
        this.specialist = specialist;
        this.workTime = workTime;
        this.qualification = qualification;
        this.room = room;
    }

    public void newDoctor() {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter Doctor ID: ");
        id = input.nextLine();

        System.out.print("Enter Doctor Name: ");
        name = input.nextLine();

        System.out.print("Enter Specialist: ");
        specialist = input.nextLine();

        System.out.print("Enter Work Time: ");
        workTime = input.nextLine();

        System.out.print("Enter Qualification: ");
        qualification = input.nextLine();

        System.out.print("Enter Room No: ");
        room = input.nextInt();
    }

    public String showDoctorInfo() {
        return id + " " + name + " " + specialist + " " +
               workTime + " " + qualification + " " + room;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSpecialist() { return specialist; }
    public String getWorkTime() { return workTime; }
    public String getQualification() { return qualification; }
    public int getRoom() { return room; }
}
