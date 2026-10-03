package application;

import java.util.Scanner;

public class Medical {

    private String name;
    private String manufacturer;
    private String expiryDate;
    private int cost;
    private int count;

    public Medical(String name, String manufacturer,
                   String expiryDate, int cost, int count) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.expiryDate = expiryDate;
        this.cost = cost;
        this.count = count;
    }

    public void newMedical() {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter Medical Name: ");
        name = input.nextLine();

        System.out.print("Enter Manufacturer: ");
        manufacturer = input.nextLine();

        System.out.print("Enter Expiry Date: ");
        expiryDate = input.nextLine();

        System.out.print("Enter Cost: ");
        cost = input.nextInt();

        System.out.print("Enter Quantity: ");
        count = input.nextInt();
    }

    public String findMedical() {
        return name + " " + manufacturer + " " +
               expiryDate + " " + cost + " " + count;
    }

    public String getName() { return name; }
    public String getManufacturer() { return manufacturer; }
    public String getExpiryDate() { return expiryDate; }
    public int getCost() { return cost; }
    public int getCount() { return count; }
}
