import java.io.*;
import java.util.*;

public class SymbolHashMap {
    private String scopeName;
    private LinkedHashMap<String, Symbol> lhm;
    //<String, Symbol> i.e. <VariableName, SymbolObject>

    public SymbolHashMap(String name) {
	lhm = new LinkedHashMap<String, Symbol>(); 
	scopeName = name;
    }
    
    public void newSymbol(String name, Symbol sym) {
	if (!lhm.containsKey(name)) {
	    lhm.put(name, sym);
	}
	else {
	    System.out.println("DECLARATION ERROR "+name);
	}
    }
    public void printSymbolHashMap() {
	System.out.println("Symbol table "+scopeName);
	Iterator<String> ii = lhm.keySet().iterator();
	while (ii.hasNext()) {
	    String variable_name = ii.next();
	    lhm.get(variable_name).printSymbol();
	}
    }
    public String getScopeName(){
	return scopeName;
    }
}