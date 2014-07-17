import java.io.*;
import java.util.*;

public class SymbolHashMap {
    String scopeName;
    LinkedHashMap<String, Symbol> lhm;
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
	    System.exit(1);
	}
    }
    public String findSymbolType(String symboltofind) {
	Symbol sym;
	if (lhm.containsKey(symboltofind)){
	    sym = lhm.get(symboltofind);
	}
	else {
	    return "";
	}
	return sym.getType();
    }
    public String findSymbolValue(String symboltofind) {
	//CAUTION: ONLY USED FOR VARIABLES OF TYPE STRING
	Symbol sym;
	if (lhm.containsKey(symboltofind)){
	    sym = lhm.get(symboltofind);
	}
	else {
	    return "";
	}
	return sym.getValue();
    }
    public void createTinyInstr() {
	Iterator<String> ii = lhm.keySet().iterator();
	while (ii.hasNext()) {
	    TinyInstr instr;
	    String variable_name = ii.next();
	    String vartype = findSymbolType(variable_name);
	    if (vartype == "STRING") {
		instr = new TinyInstr("str", variable_name, findSymbolValue(variable_name));
	    }
	    else { //it's an INT or a FLOAT
		instr = new TinyInstr("var", variable_name, "");
	    }
	    System.out.println(instr.printInstr());
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