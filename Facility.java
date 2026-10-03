package application;

import java.util.Scanner;

public class Facility {

    private String facility;
    
    //constructor
    public Facility(String facility) {
        this.facility = facility;
    }
    
    //Add new facility
    public void newFacility() {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter Facility: ");
        facility = input.nextLine();
    }
    
    // Show facility
    public String showFacility() {
        return facility;
    }

    //getter
    public String getFacility() { return facility; }
}
