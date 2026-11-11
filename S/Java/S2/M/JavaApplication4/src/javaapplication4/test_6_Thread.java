package javaapplication4;


class MyThread extends Thread {
    
    @Override
    public void run() {
        System.out.println("Thread is running....");
    }
    
}


public class test_6_Thread {
    
    public static void main(String[] args) {

        
        MyThread t1 = new MyThread();
        t1.start();
        
        
    }
    
}
