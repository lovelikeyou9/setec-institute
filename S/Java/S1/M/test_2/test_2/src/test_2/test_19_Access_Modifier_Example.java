package test_2;

public class test_19_Access_Modifier_Example {

    public static void main(String[] args) {

        test_19_AccessDemo obj = new test_19_AccessDemo();

        // obj.privateVar ❌ ERROR (private)
        
        System.out.println(obj.defaultVar);     // ✅ same package
        System.out.println(obj.protectedVar);   // ✅ same package
        System.out.println(obj.publicVar);      // ✅ everywhere

        // obj.privateMethod(); ❌ ERROR
        
        obj.defaultMethod();    // ✅
        obj.protectedMethod();  // ✅
        obj.publicMethod();     // ✅
        
        
    }
}
