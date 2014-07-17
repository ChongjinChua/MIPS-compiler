import java.util.*;
import java.io.*;

//NOTE::: "X.addAll(Y)" is the same as (X)U(Y)
//AND ::: "X.removeAll(Y)" is the same as (X)\(Y)

//ALSO NOTE that the use of the terms "IN set" and "OUT set" are opposite to 
//those described in the notes. They are just flipped around...since we are 
//doing backward analysis, it is easier to wrap the mind around them.

public class Liveness {
    static Boolean DEBUG = false;
    public static void DoIt() {
	GenCFG();
	Analysis();

	/*
	//Print CFG:
	for(int nf = 0; nf < FunctionList.FuncList.size(); nf++) {
	for(int i = 0; i < FunctionList.FuncList.get(nf).FuncIR.getSize(); i++) {
	IRNode curr = FunctionList.FuncList.get(nf).FuncIR.getNode(i);     
	System.err.println(curr.printNode());		  
	curr.printPredSucc();
	curr.printLiveSet();
	System.err.println();
	}}*/
    }

    //PERFORM LIVENESS ANALYSIS BY FILLING IN THE IN AND OUT SETS
    public static void Analysis() {
	int i;
	IRNode curr = null;
	String opcode, op1, op2, res;
	LinkedList<IRNode> worklist = new LinkedList<IRNode>(); //FIFO QUEUE IMPLEMENTATION
	for(int nf = FunctionList.FuncList.size()-1; nf >= 0; nf--) {

	    worklist.clear();
	    //Initially, add all the nodes to the worklist in backwards order (RET first)
	    for(i = FunctionList.FuncList.get(nf).FuncIR.getSize()-1; i>=0; i--) {
		curr = FunctionList.FuncList.get(nf).FuncIR.getNode(i);
		worklist.add(curr);
	    }

	    //Then process each node in worklist.
	    //if a node's outset changes, add it back to the worklist.
	    Iterator<IRNode> W = worklist.iterator();
	    IRNode N = null;
	    while(W.hasNext()) { //while worklist is not empty
		Boolean changed = false;
		N = worklist.remove(); //pop off first node in queue		

		//Process the node:
		op1 = N.op1;
		op2 = N.op2;
		opcode = N.opcode;
		res = N.res;

		//SPECIAL CASE:
		if (opcode.equals("RET")) {
		    //initialize insets of RETs to all global variables
		    //since global variables MAY be used after the function returns
		    //again, just being safe.
		    N.InSet.addAll(GetVariablesAsSet());
		}

		//N_in = U(all S_outs)
		Iterator<IRNode> succ = N.Successors.iterator();
		while(succ.hasNext()) {
		    N.InSet.addAll(succ.next().OutSet);
		}
		
		HashSet<IRNode> tempcpy = (HashSet<IRNode>) N.OutSet.clone();
		
		//N_out = N_in + GEN - KILL
		if (N.OutSet.addAll(N.InSet)) { 
		    if(DEBUG)System.err.print("Changed by INSET...");
		    changed = true;
		}

		//GENERATES:
		if (N.OutSet.addAll(Generates(opcode, op1, op2, res))) { 
		    if(DEBUG)System.err.print("Changed by GEN...");
		    changed = true;
		}

		//KILLS:
		if (N.OutSet.removeAll(Kills(opcode,op1,op2,res))) { 
		    if(DEBUG)System.err.print("Changed by KILL...");
		    changed = true;
		}
		if (changed) {
		    addBacktoWorklist(worklist,N); 
		}
		if(DEBUG)System.err.println(";"+N.printNode());
	    }
	    if(DEBUG)System.err.println();
	}
    }

    public static void addBacktoWorklist(LinkedList<IRNode> worklist, IRNode N) {
	//if N_out changed, put all of N's predecessors back onto the worklist
	Iterator<IRNode> iter = N.Predecessors.iterator();
	while(iter.hasNext()) {
	    IRNode pred = iter.next();
	    if(DEBUG)System.err.print(">"+pred.printNode());
	    if (!worklist.contains(pred)) {
		worklist.add(pred);
		if(DEBUG)System.err.println("put back on list");
	    }
	    else {
		if(DEBUG)System.err.println("already on list");
	    }
	}
    }

    // FILL IN THE CFG BY LINKING ALL LEADER NODES TO OTHER NODES
    public static void GenCFG() {
	int i;
	IRNode prev = null; //previous IR Node in list
	IRNode next = null; //next IR Node in list
	IRNode curr = null; //current IR Node being worked on

	if(DEBUG)System.err.println("");
	for(int nf = 0; nf < FunctionList.FuncList.size(); nf++) {
	    for(i = 0; i < FunctionList.FuncList.get(nf).FuncIR.getSize(); i++) {
		curr = FunctionList.FuncList.get(nf).FuncIR.getNode(i);
		
		if (prev!=null && 
		    !(prev.opcode.equals("JUMP") ||
		      prev.opcode.equals("RET"))) {
		    //if it's a jump, its only successor should be the one it is pointing to
		    //(i.e. it does not have a fall-through)
		    //and the next node should not have jump as its pred
		    //Same for RET, the RET should be the end of a block and the node after RET should not have any predecessors
		    if (curr != null) prev.addSucc(curr);
		    curr.addPred(prev);
		}

		//CONDITIONAL JUMPS:
                if(curr.opcode.equals("GE") ||
		   curr.opcode.equals("LE") ||
		   curr.opcode.equals("GT") ||
		   curr.opcode.equals("LT") ||
		   curr.opcode.equals("EQ") ||
		   curr.opcode.equals("NE")
		   ){
		    //see node.res (will contain label to jump to)
		    String label = curr.res;
		    next = searchForLabelNode(label);
		    curr.addSucc(next);
		    next.addPred(curr);
		}
		//UNCONDITIONAL JUMPS: (excluding JSR and RET)
		else if (curr.opcode.equals("JUMP")) {
		    //exact same as above, only difference is label is stored in curr.op1
		    String label = curr.op1;
		    next = searchForLabelNode(label);
		    if (next == null) { System.err.println("Could not find "+label); }
		    curr.addSucc(next);
		    next.addPred(curr);
		}
		else if (curr.opcode.equals("JSR")) {
		    String label = curr.op1;
		    next = searchForLabelNode(label);
		    if (next == null) { System.err.println("Could not find "+label); }
		    //For JSR, the function must be its own basic block
		    //So the beginning node of a function (LABEL main, etc) should not have any 
		    //predecessors
		    next.clearPred();
		}
		//traverse:
		prev = curr;
	    }
	}
    }

    private static HashSet Generates(String opcode, String op1, String op2, String res) {
	HashSet<String> hs = new LinkedHashSet<String>();
	if (opcode.startsWith("WRITE") ||
	    opcode.startsWith("STORE") ||
	    opcode.startsWith("ADD") ||
	    opcode.startsWith("SUB") ||
	    opcode.startsWith("MULT") ||
	    opcode.startsWith("DIV") ||
	    opcode.equals("PUSH")) {
	    if (!op1.isEmpty() && isVariableOrTemporary(op1)) {
		hs.add(op1);
	    }
	    if (!op2.isEmpty() && isVariableOrTemporary(op2)) {
		hs.add(op2);
	    }
	}
	if(opcode.equals("GE") ||
	   opcode.equals("LE") ||
	   opcode.equals("GT") ||
	   opcode.equals("LT") ||
	   opcode.equals("EQ") ||
	   opcode.equals("NE")
	   ){
	    if (!op1.isEmpty() && isVariableOrTemporary(op1)) {
		hs.add(op1);
	    }
	    if (!op2.isEmpty() && isVariableOrTemporary(op2)) {
		hs.add(op2);
	    }
	}
	if (opcode.equals("JSR")) {
	    //we do not know what variables may be used inside the function
	    //so being conservative: assume all global variables may be used (GEN=globals)
	    //and no variables must be defined (KILL={})
	    hs.addAll(GetVariablesAsSet());
	}
	
	return hs;
    }

    private static HashSet Kills(String opcode, String op1, String op2, String res) {
	HashSet<String> hs = new LinkedHashSet<String>();
	if (opcode.startsWith("ADD") ||
	    opcode.startsWith("SUB") ||
	    opcode.startsWith("MULT") ||
	    opcode.startsWith("DIV") ||
	    opcode.startsWith("READ") ||
	    opcode.startsWith("STORE")) {
	    if (!res.isEmpty() && isVariableOrTemporary(res)) {
		hs.add(res);
	    }
	}
	if (opcode.startsWith("POP")) {
	    if (!op1.isEmpty() && isVariableOrTemporary(op1)) {
		hs.add(op1);
	    }
	}
	return hs;
    }

    public static IRNode searchForLabelNode(String label) {
	IRNode node = ExprStack.AllLabelNodes.get(label);
	return node;
    }

    public static Set<String> GetVariablesAsSet() {
	//gets the variables in the global scope
	//could expand this method to get variables of any given function...but eh
	
	//get the first item in list -> it's gonna be the global function
	FunctionObject func = FunctionList.FuncList.get(0);
	
	//Get the symbol table of this function
	LinkedHashMap<String,Symbol> shm = func.func_table.lhm; //idk why it's called func_table..
	Set<String> retset = shm.keySet();
	if(DEBUG)System.err.println(retset.toString());
	return retset;
    }

    private static Boolean isVariableOrTemporary(String op) {
	//sees whether the value is one of: variable name, temporary, local, parameter, return value
	//and not one of: digits, floats
	if (op.startsWith("$")) { return true; } //$T, $L, $P, or $R
	if (op.matches("[0-9]+.?([0-9])*")) { return false; } //0-9, 0.0-9.9
	else { return true; } //abcd
    }
}