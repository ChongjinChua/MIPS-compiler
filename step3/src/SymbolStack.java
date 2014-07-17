import java.util.*;
import java.io.*;

public class SymbolStack {
    private Stack<SymbolHashMap> symstack;
    
    public SymbolStack() {
	symstack = new Stack<SymbolHashMap>();
    }
    
    public void push(SymbolHashMap shm) {
	symstack.push(shm);
    }

    public SymbolHashMap pop() {
	if (!symstack.isEmpty()) {	    
	    return symstack.pop();
	}
	return null;
    }

    public void printSymbolStack() {
	while (!symstack.isEmpty()) {
	    SymbolHashMap currScope = symstack.pop();
	    currScope.printSymbolHashMap();
	}
    }
}