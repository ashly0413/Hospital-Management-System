package application;

import java.util.Scanner;

public class Lab {

    private String lab;
    private int cost;

    public Lab(String lab, int cost) {
        this.lab = lab;
        this.cost = cost;
    }

    public void newLab() {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter Laboratory: ");
        lab = input.nextLine();

        System.out.print("Enter Cost: ");
        cost = input.nextInt();
    }

    public String labList() {
        return lab + " " + cost;
    }

    public String getLab() { return lab; }
    public int getCost() { return cost; }
    
}
