import java.util.*;
import java.io.*;

public class ParLocalRegisters {
    static Map<String,String> registers = new HashMap<String, String>();
    
    public static void putRegister(String name,String regtype) {
        registers.put(name,regtype);
        //return "$T"+Integer.toString(registers.size());
    }
    public static String getType(String register) {
        //get the register number (strip off the $T)
        return registers.get(register);
    }
    /*
     private static int registercount = 1;
     public static String getRegister() {
     return "$T"+Integer.toString(registercount);
     }
     public static String newRegister() {
     return "$T"+Integer.toString(registercount++);
     }
     */
}