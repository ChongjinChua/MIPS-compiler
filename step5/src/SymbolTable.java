import java.util.*;
import java.io.*;

/* This is main class.
 * SymbolStack is a Stack, each element of which points to a SymbolHashMap.
 * SymbolHashMap is a LinkedHashMap that stores Symbol objects in the order they
 *  were inserted.
 * Symbol is the base object that stores the name, type, and value of 
 *  the variable parsed.
 */

public class SymbolTable {
    private static int block_count = 0;
    private static LinkedList<SymbolHashMap> stack = new LinkedList<SymbolHashMap>();

    public static void insertSymbol(String name, String idtype, String value) {
	//for cases where name comes out to be a list (e.g. x,y,z)
	SymbolHashMap shm = stack.pop();
	String[] namelist = name.split(",");
	for (int ii = 0; ii < namelist.length; ii++) {
	    Symbol sym = new Symbol(namelist[ii], idtype, value);
	    shm.newSymbol(sym.getName(), sym);
	}
	stack.push(shm);
    }

    public static String getType(String key) {
	SymbolHashMap shm;
	String symboltype = "";
	
	for (int i = 0; i < stack.size(); i++) {
	    shm = stack.get(i);
	    symboltype = shm.findSymbolType(key);
	    if (symboltype != "") { //key has been found	
		break;
	    }
	}
	if (symboltype.equals("INT")) {
	    return "I";
	}
	else if (symboltype.equals("FLOAT")) {
	    return "F";
	}
	else if (symboltype.equals("STRING")) {
	    return "S";
	}
	else {
	    return "";
	}
	//symboltype can never return null
	//this case should have been taken care of by parser
    }
    
    public static void createScope(String scopeName) {
	//System.out.println("CREATING SCOOOOOOPE");
	if (scopeName == "BLOCK") {
	    block_count++;
	    SymbolHashMap shm = new SymbolHashMap
		(scopeName+" "+Integer.toString(block_count));
	    stack.push(shm);
	}
	else {
	    SymbolHashMap shm = new SymbolHashMap(scopeName);
	    stack.push(shm);
	}
    }
    
    public static SymbolHashMap peekScope() {
	return stack.peek();
    }

    public static void popScope() { //pops scope off stack when done reading that scope
	if (!stack.isEmpty())
	    stack.pop();
	else
	    return;
    }

    public static void createTinyInstr() {
	SymbolHashMap currScope = stack.peek();
	currScope.createTinyInstr();
	return;
    }

    public static void printSymbolTable() {
	//	stack.printSymbolStack();
	while (!stack.isEmpty()) {
	    SymbolHashMap currScope = stack.pop();
	    currScope.printSymbolHashMap();
	}
	System.out.println();//add extra line between scopes
    }

    /*
    public static void main(String args[]) {
	//GET NEW VARIABLE
	Symbol sym = new Symbol("X", "INT", "1");
	Symbol sym2 = new Symbol("Y", "FLOAT", "0.22");
	sym.printSymbol();
	sym2.printSymbol();

	//ADD VARIABLE TO HASHMAP
	SymbolHashMap shm = new SymbolHashMap();
	shm.newSymbol(sym.getName(), sym);
	shm.newSymbol(sym2.getName(), sym2);
	shm.newSymbol(sym.getName(), sym2);
	//	shm.printSymbolHashMap();

	//ADD HASHMAP TO STACK
	SymbolStack stack = new SymbolStack();
	stack.push(shm);
	stack.printSymbolStack();
	}
*/
}
