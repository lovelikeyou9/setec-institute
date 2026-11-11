/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package test_2;

/**
 *
 * @author Admin
 */
public class test_3_Condition_IF {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        int number = 18;
        
        if(number>=18){
            System.out.println("You are an adult.");
        }else{
            System.out.println("You are not an adult.");
        }
        
        
        int a = -5;
        boolean x = true;
        String t="setec";
        
        if(a<0)
            System.out.println("a is positive.");
        
        if(a>0)
            System.out.println("a is positive.");
        
        if(x)
            System.out.println("x is positive.");
        
        if(t.equals("Setec"))
            System.out.println("t   is positive.");
        
        if(t.equals("setec"))
            System.out.println("t is positive.");
        
        if(a>0)
            System.out.println("a is positive.");
        else if(a<0)
            System.out.println("a is negative.");
        else
            System.out.println("a is zero.");
        
    }
    
}
