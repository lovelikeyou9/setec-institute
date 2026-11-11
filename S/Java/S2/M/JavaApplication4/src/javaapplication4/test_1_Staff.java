package javaapplication4;

import java.util.Scanner;

public class test_1_Staff {
    private int id;
    private String name;
    private String sex;
    private String phone;

    // Getter and Setter for ID
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // Getter and Setter for Name
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Getter and Setter for Sex
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    // Getter and Setter for Phone
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    // Input method
    public void inputStaff(Scanner sc) {
        System.out.print("Enter ID: ");
        setId(sc.nextInt());
        sc.nextLine(); // consume newline

        System.out.print("Enter Name: ");
        setName(sc.nextLine());

        System.out.print("Enter Sex: ");
        setSex(sc.nextLine());

        System.out.print("Enter Phone: ");
        setPhone(sc.nextLine());
    }

    // Display method
    public void displayStaff() {
        System.out.println("ID: " + getId());
        System.out.println("Name: " + getName());
        System.out.println("Sex: " + getSex());
        System.out.println("Phone: " + getPhone());
    }
}
