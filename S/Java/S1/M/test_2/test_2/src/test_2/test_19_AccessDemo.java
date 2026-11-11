package test_2;

public class test_19_AccessDemo {

    private int privateVar = 10;
    int defaultVar = 20;          // default
    protected int protectedVar = 30;
    public int publicVar = 40;

    private void privateMethod() {
        System.out.println("Private Method");
    }

    void defaultMethod() {
        System.out.println("Default Method");
    }

    protected void protectedMethod() {
        System.out.println("Protected Method");
    }

    public void publicMethod() {
        System.out.println("Public Method");
    }

    public void showAll() {
        // All accessible inside the same class
        System.out.println(privateVar);
        System.out.println(defaultVar);
        System.out.println(protectedVar);
        System.out.println(publicVar);
    }
}
