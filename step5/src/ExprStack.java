import java.io.*;
import java.util.*;
import java.lang.*;

public class ExprStack {
    private static Stack<String> stack = new Stack<String>(); //stack of IR Nodes
    public static int labelcnt = 0;
    public static LinkedList<Integer> labelStack = new LinkedList<Integer>(); //stack of labelStack, used for jumps and conditionals    
    public static void addOperatorIR(String op) {
        //this method only for +-/*
        String op2 = stack.pop();
        String op1 = stack.pop();
        
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
        //	System.out.println("Type: "+optype);
        String res = ValueRegister.newRegister(optype);
        stack.push(res);
        
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
        IRList.AddNode(node);
        //System.out.println("Pushed result: "+printTopofStack());
    }
    public static void evaluateExprIR() {
        //this method only for :=
        //e.g. "a := 20" --> "STOREI $T1 a"
        String res = stack.pop();
        String op1 = stack.pop();
        
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
        else {
            optype = SymbolTable.getType(op1);	}
        
        IRNode node = new IRNode("STORE"+optype, op1, "", res);
        //	System.out.println(";; "+node.printNode());
        IRList.AddNode(node);
        
        //	stack.push(res);
        //DON'T NEED TO PUSH BACK ONTO STACK WHEN AN ASSIGNMENT IS DONE
        //BECAUSE WE ARE DONE WITH THE EXPRESSION
    }
    public static void addLiteralIR(String lit) {
        //this method only for when literal is read
        //e.g. '20', these will always convert into e.g STOREI 20 $T1
        String op1 = lit;
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
        else {
            optype = SymbolTable.getType(op1); }
        String res = ValueRegister.newRegister(optype);
        
        IRNode node = new IRNode("STORE"+optype, op1, "", res);
        //	System.out.println(";; "+node.printNode());
        IRList.AddNode(node);
        stack.push(res);
        //System.out.println("Pushed literal: "+printTopofStack());
    }
    public static void addRIdentifier(String id) {
        //this method only for when an R variable is read
        stack.push(id);
        //System.out.println("Pushed R: "+printTopofStack());
    }
    public static void addWriteIR(String id)
    {
        String[] idlist = id.split(",");
        for (int ii = 0; ii < idlist.length; ii++) {
            String idtype = SymbolTable.getType(idlist[ii]);
            IRNode node = new IRNode("WRITE"+idtype, idlist[ii], "", "");
            IRList.AddNode(node);
        }
    }
    public static void addReadIR(String id)
    {
        String[] idlist = id.split(",");
        for (int ii = 0; ii < idlist.length; ii++) {
            String idtype = SymbolTable.getType(idlist[ii]);
            IRNode node = new IRNode("READ"+idtype, idlist[ii], "", "");
            IRList.AddNode(node);        
        }
    }
    public static void addTrueIR()
    {
	//translates a (TRUE) to a (1 = 1)
        String res1 = ValueRegister.newRegister("I");
        IRNode node1 = new IRNode("STOREI", "1" , "", res1);
        IRList.AddNode(node1);
        //might need to push res1 & res2
        String res2 = ValueRegister.newRegister("I");
        IRNode node2 = new IRNode("STOREI", "1" , "", res2);
        IRList.AddNode(node2);
        //
        String label = "label"+getCurrentLabel();
        IRNode node = new IRNode("NE", res1 , res2, label);
        IRList.AddNode(node);
    }
    public static void addFalseIR()
    {
	//translates a (FALSE) to a (1 = 0)
        String res1 = ValueRegister.newRegister("I");
        IRNode node1 = new IRNode("STOREI", "1" , "", res1);
        IRList.AddNode(node1);
        String res2 = ValueRegister.newRegister("I");
        IRNode node2 = new IRNode("STOREI", "0" , "", res2);
        IRList.AddNode(node2);
        String label = "label"+getCurrentLabel();
        IRNode node = new IRNode("NE", res1 , res2, label);
        IRList.AddNode(node);
    }
    public static void addInvConditionalIR(String compop)
    {
        IRNode node;
        String op2 = stack.pop();
        String op1 = stack.pop();
	// String res = ValueRegister.newRegister("I");  //might need changes
	//pushLabel();
        String label = "label"+peekTopLabel();
        if (compop.equals("<")) {
	    node = new IRNode("GE", op1 , op2, label);
	    IRList.AddNode(node);
	}
	else if (compop.equals(">")) {
	    node = new IRNode("LE", op1 , op2, label);
	    IRList.AddNode(node);
	}
	else if (compop.equals("=")) {
	    node = new IRNode("NE", op1 , op2, label);
	    IRList.AddNode(node);
	}
	else if (compop.equals("!=")) {
	    node = new IRNode("EQ", op1 , op2, label);
	    IRList.AddNode(node);
	}
	else if (compop.equals("<=")) {
	    node = new IRNode("GT", op1 , op2, label);
	    IRList.AddNode(node);
	}
	else if (compop.equals(">=")) {
	    node = new IRNode("LT", op1 , op2, label);
	    IRList.AddNode(node);
	}        
    }
    //JUMP
    public static void addJumpIR(int labelcount)
    {
	String label = "label"+Integer.toString(labelcount);
        IRNode node = new IRNode("JUMP", label , "", "");
        IRList.AddNode(node);
    }
    //LABEL
    public static void addLabelIR( int labelcount)
    {
	String label = "label"+Integer.toString(labelcount);
        IRNode node = new IRNode("LABEL", label , "", "");
        IRList.AddNode(node);
    }
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
        stack.push(id);
        //System.out.println("Pushed L: "+printTopofStack());
    }
    public static String printTopofStack(){
        return stack.peek();
    }
}