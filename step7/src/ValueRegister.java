import java.util.*;
import java.io.*;

public class ValueRegister {   
    static List<String> registers = new ArrayList<String>();
    public static String getRegister() {
	return "$T"+Integer.toString(registers.size());
    }
    public static String newRegister(String regtype) {
	registers.add(regtype);
	return "$T"+Integer.toString(registers.size());
    }
    public static String getType(String register) {
	//get the register number (strip off the $T)
	String regindx = register.replace("$T", "");

	//convert into int and find the type of that register;
	return registers.get(Integer.parseInt(regindx) - 1);
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