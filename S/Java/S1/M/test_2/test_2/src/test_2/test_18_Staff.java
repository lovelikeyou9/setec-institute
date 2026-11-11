package test_2;

public class test_18_Staff {
    protected int id;
    protected String name;
    protected String sex;
    protected String phone;

    public void inputStaff(java.util.Scanner sc) {
        System.out.print("Enter ID: ");
        id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Name: ");
        name = sc.nextLine();

        System.out.print("Enter Sex: ");
        sex = sc.nextLine();

        System.out.print("Enter Phone: ");
        phone = sc.nextLine();
    }

    public void displayStaff() {
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Sex: " + sex);
        System.out.println("Phone: " + phone);
    }
    
}
