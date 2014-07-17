import java.io.*;
import java.util.*;
import java.lang.*;

public class ExprStack {
    //private static Stack<String> stack = new Stack<String>(); //stack of IR Nodes
    public static int labelcnt = 0;
    public static int pushcnt = 0;
    public static LinkedList<Integer> labelStack = new LinkedList<Integer>(); //stack of labelStack, used for jumps and conditionals
    public static FunctionObject top ;
    
    public static HashMap<String, IRNode> AllLabelNodes = new HashMap<String, IRNode>(); //storing each LABEL node created so that we can use it to do liveness analysis in Liveness.java

    public static void addOperatorIR(String op) {
        //this method only for +-/*
        top = FunctionList.top;
	if (top.stack.size() < 2) {
	    return;
	}
	String op2 = top.stack.pop();
	String op1 = top.stack.pop();
        
	//GET TYPE OF OPERATION, whether I or F
	String optype = "";
	if (op1.startsWith("$T")) { //if it is a register,
	    //so find register type
	    //	    System.out.print("Register "+op1);
	    optype = ValueRegister.getType(op1);
	}
	else if (op1.matches("[0-9]+")) { //if it's an int
	    optype = "I";
	}
	else if (op1.contains(".")) { //if it's a float
	    optype = "F";
	}
	else { //it is a symbol/literal
	    //so find symbol type
	    optype = SymbolTable.getType(op1);
	    //	    optype = SymbolTable.getType.
	}
	if(optype == "")
	    {
		optype = ParLocalRegisters.registers.get(op1);
	    }
	//	System.out.println("Type: "+optype);
	String res = ValueRegister.newRegister(optype);
	top.stack.push(res);
        
	//String optype = searchSymbolStack();
        
	//GET INSTRUCTION
	String instruction = "";
	if (op.equals("+")) {
	    instruction = "ADD"; }
	else if (op.equals("-")) {
	    instruction = "SUB"; }
	else if (op.equals("*")) {
	    instruction = "MULT";}
	else if (op.equals("/")) {
	    instruction = "DIV"; }
        
	IRNode node = new IRNode(instruction+optype, op1, op2, res);
	//	System.out.println(";; "+node.printNode());
	top.FuncIR.AddNode(node);
	//System.out.println("Pushed result: "+printTopofStack());	
    }
    public static void evaluateExprIR() {
        //this method only for :=
        //e.g. "a := 20" --> "STOREI $T1 a"
        top = FunctionList.top;

	//there must be at least 2 elements on the stack
	if (top.stack.size() < 2) { return; }

	String res = top.stack.pop();
	String op1 = top.stack.pop();	
        
	//GET TYPE OF OPERATION, whether I or F
	String optype = "";
	if (res.startsWith("$T")) {
	    optype = ValueRegister.getType(op1); }
	else if (op1.matches("[0-9]+")) {
	    optype = "I";
	}
	else if (op1.contains(".")) {
	    optype = "F";
	}
	else
	    {
		optype = SymbolTable.getType(res);
	    }
	if(optype == "")
	    {
		optype = ParLocalRegisters.registers.get(res);
	    }
        
	IRNode node = new IRNode("STORE"+optype, op1, "", res);
	//	System.out.println(";; "+node.printNode());
	top.FuncIR.AddNode(node);
        
	//	stack.push(res);
	//DON'T NEED TO PUSH BACK ONTO STACK WHEN AN ASSIGNMENT IS DONE
	//BECAUSE WE ARE DONE WITH THE EXPRESSION
	    
    }
    public static void addLiteralIR(String lit) {
        //this method only for when literal is read
        //e.g. '20', these will always convert into e.g STOREI 20 $T1
        top = FunctionList.top;
        String op1 = lit;
        //System.out.println("func oooii9999"+top.name);
        //String res = ValueRegister.newRegister("I");
        
        //GET TYPE OF OPERATION, whether I or F
        String optype = "";
        if (op1.startsWith("$T")) {
            optype = ValueRegister.getType(op1); }
        else if (op1.matches("[0-9]+")) {
            optype = "I";
        }
        else if (op1.contains(".")) {
            optype = "F";
        }
        else
	    {
		optype = SymbolTable.getType(op1);
	    }
        if(optype == "")
	    {
		optype = ParLocalRegisters.registers.get(op1);
	    }
        String res = ValueRegister.newRegister(optype);
        
        IRNode node = new IRNode("STORE"+optype, op1, "", res);
        top.FuncIR.AddNode(node);
        top.stack.push(res);
        //System.out.println("Pushed literal: "+printTopofStack());
    }
    public static void addRIdentifier(String id) {
        //this method only for when an R variable is read
        top = FunctionList.top;
        Symbol sym = top.func_table.lhm.get(id);
	// System.out.println("Added reg :"+sym.getReg());	
	if (sym == null) { 
	    //if couldn't find it in current scope,
	    //look in a higher scope (usually just
	    //one scope higher - ie the GLOBAL scope)
		
	    FunctionObject temptop = FunctionList.PeekGlobal();
	    sym = temptop.func_table.lhm.get(id);
	}
	if (sym == null) { //if symbol is still not found
	    System.err.println("Could not find variable "+id);
	    System.exit(0);
	}
	ParLocalRegisters.registers.put(sym.getReg(),sym.getType().substring(0,1));
	top.stack.push(sym.getReg());
	//System.out.println("Pushed R: "+printTopofStack());	
    }
    public static void addWriteIR(String id)
    {
        top = FunctionList.top;
        String[] idlist = id.split(",");
        for (int ii = 0; ii < idlist.length; ii++) {
            String idtype = SymbolTable.getType(idlist[ii]);
            IRNode node;
            if(top.func_table.lhm.containsKey(idlist[ii]))
		{
		    String reg = top.func_table.lhm.get(idlist[ii]).reg;
		    node = new IRNode("WRITE"+idtype, reg, "", "");
		}
            else
		{
		    node = new IRNode("WRITE"+idtype, idlist[ii], "", "");
		}
            top.FuncIR.AddNode(node);
        }
    }
    public static void addReadIR(String id)
    {
        top = FunctionList.top;
        String[] idlist = id.split(",");
        for (int ii = 0; ii < idlist.length; ii++) {
            String idtype = SymbolTable.getType(idlist[ii]);
            IRNode node;
            if(top.func_table.lhm.containsKey(idlist[ii]))
		{
		    String reg = top.func_table.lhm.get(idlist[ii]).reg;
		    node = new IRNode("READ"+idtype, reg, "", "");
		}
            else
		{
		    node = new IRNode("READ"+idtype, idlist[ii], "", "");
		}
            top.FuncIR.AddNode(node);
        }
    }
    public static void addTrueIR()
    {
	//translates a (TRUE) to a (1 = 1)
        top = FunctionList.top;
        String res1 = ValueRegister.newRegister("I");
        IRNode node1 = new IRNode("STOREI", "1" , "", res1);
        top.FuncIR.AddNode(node1);
        //might need to push res1 & res2
        String res2 = ValueRegister.newRegister("I");
        IRNode node2 = new IRNode("STOREI", "1" , "", res2);
        top.FuncIR.AddNode(node2);
        //
        String label = "label"+getCurrentLabel();
        IRNode node = new IRNode("NE", res1 , res2, label);
        top.FuncIR.AddNode(node);
    }
    public static void addFalseIR()
    {
	//translates a (FALSE) to a (1 = 0)
        top = FunctionList.top;
        String res1 = ValueRegister.newRegister("I");
        IRNode node1 = new IRNode("STOREI", "1" , "", res1);
        top.FuncIR.AddNode(node1);
        String res2 = ValueRegister.newRegister("I");
        IRNode node2 = new IRNode("STOREI", "0" , "", res2);
        top.FuncIR.AddNode(node2);
        String label = "label"+getCurrentLabel();
        IRNode node = new IRNode("NE", res1 , res2, label);
//        node.predecessors.add(top.FuncIR.ListTop());       
        top.FuncIR.AddNode(node);
    }
    public static void addInvConditionalIR(String compop)
    {
        top = FunctionList.top;
        IRNode node;
	if (top.stack.size() < 2) { 
	    System.out.println(top.stack.pop());
	    return; } //must have at least 2 elements
        String op2 = top.stack.pop();
        String op1 = top.stack.pop();
	// String res = ValueRegister.newRegister("I");  //might need changes
	//pushLabel();
        String label = "label"+peekTopLabel();
        if (compop.equals("<")) {
	    node = new IRNode("GE", op1 , op2, label);
//        node.predecessors.add(top.FuncIR.ListTop());
	    top.FuncIR.AddNode(node);
	}
	else if (compop.equals(">")) {
	    node = new IRNode("LE", op1 , op2, label);
//        node.predecessors.add(top.FuncIR.ListTop());
	    top.FuncIR.AddNode(node);
	}
	else if (compop.equals("=")) {
	    node = new IRNode("NE", op1 , op2, label);
//        node.predecessors.add(top.FuncIR.ListTop());
	    top.FuncIR.AddNode(node);
	}
	else if (compop.equals("!=")) {
	    node = new IRNode("EQ", op1 , op2, label);
//        node.predecessors.add(top.FuncIR.ListTop());
	    top.FuncIR.AddNode(node);
	}
	else if (compop.equals("<=")) {
	    node = new IRNode("GT", op1 , op2, label);
//        node.predecessors.add(top.FuncIR.ListTop());
	    top.FuncIR.AddNode(node);
	}
	else if (compop.equals(">=")) {
	    node = new IRNode("LT", op1 , op2, label);
//        node.predecessors.add(top.FuncIR.ListTop());
	    top.FuncIR.AddNode(node);
	}        
    }
    
    //JUMP
    public static void addJumpIR(int labelcount)
    {
        top = FunctionList.top;
        String label = "label"+Integer.toString(labelcount);
        IRNode node = new IRNode("JUMP", label , "", "");
        AllLabelNodes.put(label, node); //add to the global AllLabels pool
        top.FuncIR.AddNode(node);
    }
    
    //LABEL
    public static void addLabelIR( int labelcount)
    {
        top = FunctionList.top;
        String label = "label"+Integer.toString(labelcount);
        IRNode node = new IRNode("LABEL", label , "", "");
        AllLabelNodes.put(label, node); //add to the global AllLabels pool
        top.FuncIR.AddNode(node);
    }
    
    //Step 6
    public static void addLinkLabel()
    {
        top = FunctionList.top;        
        IRNode node = new IRNode("LABEL", top.name , "", "");
        AllLabelNodes.put(top.name, node); //add to the global AllLabels pool
        top.FuncIR.AddNode(node);
        IRNode node1 = new IRNode("LINK", "" , "", "");
        top.FuncIR.AddNode(node1);
    }
    //    public static void addFirstPush()
    //    {
    //        pushcnt = 0;
    //        top = FunctionList.top;
    //        IRNode node = new IRNode("PUSH", "" , "", "");
    //        top.FuncIR.AddNode(node);
    //    }
    //adding all PUSH calls before calling function
    public static void addPushCall(String expr)
    {
        String reg = "";
        if(top.push_cnt == 0)
	    {
		IRNode node3 = new IRNode("PUSH", "" , "", "");
		top.FuncIR.AddNode(node3);
	    }
        top.push_cnt++;
        top = FunctionList.top;
        if(top.func_table.lhm.containsKey(expr))
	    {
		reg = top.func_table.lhm.get(expr).reg;
	    }
        else
	    {
		//            String optype = SymbolTable.getType(op1);
		reg = ValueRegister.getRegister();
	    }
        IRNode node = new IRNode("PUSH", reg , "", "");
        top.FuncIR.AddNode(node);
    }
    //printing JSR and all the POPs
    public static void addFunctionCall(String FuncName)
    {
        String res = "";
        top = FunctionList.top;
        IRNode node = new IRNode("JSR", FuncName , "", "");
        top.FuncIR.AddNode(node);
        IRNode node1 = new IRNode("POP", "" , "", "");
        top.FuncIR.AddNode(node1);
        
        for(int i=0;i<top.push_cnt-1;i++)
	    {
		IRNode node2 = new IRNode("POP", "" , "", "");
		top.FuncIR.AddNode(node2);
	    }
        res = ValueRegister.newRegister("I");
        IRNode node3 = new IRNode("POP", res , "", "");
        top.FuncIR.AddNode(node3);
        top.stack.push(res);
	//adding Return
    }
    public static void addReturn()
    {
        top = FunctionList.top;
	if (top.stack.empty()) { System.err.println("oops"); return; }
        String op1 = top.stack.pop();
        //optype
        String optype = "";
        if (op1.startsWith("$T")) {
            optype = ValueRegister.getType(op1); }
        else if (op1.matches("[0-9]+")) {
            optype = "I";
        }
        else if (op1.contains(".")) {
            optype = "F";
        }
        else
	    {
		optype = SymbolTable.getType(op1);
	    }
        if(optype == "")
	    {
		optype = ParLocalRegisters.registers.get(op1);
	    }
        //
        
        //Storing value on temp
        IRNode node = new IRNode("STORE"+optype, op1, "", "$R");
        top.FuncIR.AddNode(node);
        top.stack.push(op1);//dont know if should push back the popped operand
        //retuning
        IRNode node1 = new IRNode("RET", "", "", "");
        top.FuncIR.AddNode(node1);        
    }
    
    public static void CheckReturn()
    {
        top = FunctionList.top;
        IRList temp = top.FuncIR;
        if(!temp.IRL.get((temp.getSize())-1).opcode.equals("RET"))
	    {
		// System.out.println("This is the Last Node: "+ temp.IRL.get((temp.IRL.size()) - 1 ).opcode);
		IRNode node1 = new IRNode("RET", "", "", "");
		top.FuncIR.AddNode(node1);
	    }
    }
    /////End Step 6
    
    public static void pushLabel() {
	//new IF block has started...add an end label to keep track
	labelStack.push(new Integer(++labelcnt));
	//create new label (i.e. increment labelcnt, turn it into object,
	//and push that onto stack)
    }
    public static int peekTopLabel() {
	//return the top of the labelStack stack
	//(stack ensures we always get endlabel of current scope)
	if (!labelStack.isEmpty()) {
	    return labelStack.peek().byteValue();
	}
	else {
	    return 0;
	}
    }
    public static int peekSecondTopLabel() {
	//return the top of the labelStack stack
	//(stack ensures we always get endlabel of current scope)
	if (!labelStack.isEmpty()) {
	    Integer l1 = labelStack.pop();
	    if (!labelStack.isEmpty()) {
		Integer l2 = labelStack.pop();
		labelStack.push(l2);
		labelStack.push(l1);	    
		return l2.byteValue();
	    }
	    return -1;
	}	
	else {
	    return -1;
	}
    }
    public static int popLabel() {
	//reached ENDIF, pop the endlabel off the stack
	if (!labelStack.isEmpty()) {
	    return labelStack.pop().byteValue();
	    //byteValue() turns Integer to int
	}
	else {
	    return -1;
	}
    }
    public static int getCurrentLabel() {
	return labelcnt;
    }
    public static void addLIdentifier(String id) {
        //this method only for when an L variable is read

	Symbol sym; //temp var for storing the constructed symbol obj
        top = FunctionList.top;
	sym = top.func_table.lhm.get(id);
	if (sym == null) { 
	    //if couldn't find it in current scope,
	    //look in a higher scope (usually just
	    // the top-most scope - the GLOBAL scope)
	    //FunctionObject temptop = FunctionList.PeekHigher(i);
	    FunctionObject temptop = FunctionList.PeekGlobal();
	    sym = temptop.func_table.lhm.get(id);
	}
	if (sym == null) { //if symbol is still not found
	    System.err.println("Could not find variable "+id);
	    System.exit(0);
	}
	String reg = sym.getReg();
	String type = sym.getType().substring(0,1);
	
	ParLocalRegisters.registers.put(reg,type);
	
	top.stack.push(reg);
	//System.out.println("Pushed L: "+printTopofStack());
    }
    public static String printTopofStack(){
        top = FunctionList.top;
        return top.stack.peek();
    }
}