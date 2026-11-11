package test_2;

public class test_17_Student {

    int id;
    String name;
    String sex;
    int SA;
    int Java;
    int Orac;

    public test_17_Student(int id, String name, String sex, int SA, int Java, int Orac) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.SA = SA;
        this.Java = Java;
        this.Orac = Orac;
    }

    // ReExam fee: $50 per subject < 50
    public int calcReExam() {
        int fee = 0;
        if (SA < 50) fee += 50;
        if (Java < 50) fee += 50;
        if (Orac < 50) fee += 50;
        return fee;
    }

    // Check if any reExam needed
    public boolean hasReExam() {
        return (SA < 50 || Java < 50 || Orac < 50);
    }

    // Which subjects need reExam
    public String getReExamSubjects() {
        String result = "";

        if (SA < 50) result += "SA ";
        if (Java < 50) result += "Java ";
        if (Orac < 50) result += "Oracle ";

        if (result.equals("")) {
            return "None";
        }

        return result.trim();
    }

    // Show normal student data
    public void displayStudentData(int schoolFee) {
        System.out.println("------------------------------------");
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Sex: " + sex);
        System.out.println("SA: " + SA);
        System.out.println("Java: " + Java);
        System.out.println("Oracle: " + Orac);
        System.out.println("School Fee: $" + schoolFee);
    }

    // Show ONLY reExam students
    public void displayReExam() {
        System.out.println("------------------------------------");
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("ReExam Subject(s): " + getReExamSubjects());
        System.out.println("ReExam Fee: $" + calcReExam());
    }
}
