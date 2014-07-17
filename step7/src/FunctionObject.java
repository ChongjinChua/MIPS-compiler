import java.io.*;
import java.util.*;
import java.lang.*;

public class FunctionObject{
    SymbolHashMap func_table;//function symbol table
    IRList FuncIR;
    static Stack<String> stack ;
    
    int par_count;
    int local_count;   
    int push_cnt;
    
    static int curr_par_count;
    static int curr_local_count;
    
    int isLocal = 0;//flag for counting local vars
    String name;
    
    public FunctionObject(String name){
        this.name = name;
        
        func_table = new SymbolHashMap(name);
        FuncIR = new IRList();
        stack = new Stack<String>(); //stack of IR Nodes
        //this.func_table.scopeName = name;
    }
    
    public void insertSymbol(String name, String idtype, String value) { 

	String[] namelist = name.split(",");	
	String reg = "";
        if(this.name.equals("GLOBAL"))
	    {
		//if it's a variable of the global scope,
		//dont convert it into a special, e.g. into $L1 or $P1
		//always use its given name as is
    		for (int ii = 0; ii < namelist.length; ii++)
		    {
			Symbol sym = new Symbol(namelist[ii], idtype, value, namelist[ii]);
			func_table.newSymbol(sym.getName(), sym);    			
		    }
	    }
        else
	    {
		reg = "$P"+Integer.toString(this.curr_par_count);
		for (int ii = 0; ii < namelist.length; ii++)
		    {
			if(this.isLocal == 1)
			    {
				this.local_count++;
				reg = "$L"+Integer.toString(this.local_count);
			    }
			// System.out.println("func "+this.name+" name "+namelist[ii]+" type "+idtype+" value "+value+"   local cnt: "+this.local_count+"   param cnt: "+this.par_count+"   reg: "+reg);
			Symbol sym = new Symbol(namelist[ii], idtype, value, reg);
			func_table.newSymbol(sym.getName(), sym);
		    }
	    }
        //stack.push(shm);
    }
}