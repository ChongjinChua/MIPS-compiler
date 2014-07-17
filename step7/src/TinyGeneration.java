import java.util.*;
import java.io.*;

public class TinyGeneration{
    //private static LinkedList<String> registerPool = new LinkedList<String>();
    private static LinkedList<TinyRegister> registerPool = new LinkedList<TinyRegister>();
    private static int registercount = 0;
    private static int returncnt = 0;
    private static int paramcnt = 0;
    static int nfg;
    private static IRNode currentNode;

    public static void buildTiny() {        
        initializeIdentifiers();
        initializations();

        int i;
        String opcode = "";
        String op1 = "";
        String op2 = "";
        String optype = "";
        for(int nf = 0; nf < FunctionList.FuncList.size(); nf++) {
            nfg = nf;
            for(i = 0; i < FunctionList.FuncList.get(nf).FuncIR.getSize(); i++) {
                IRNode currentIRNode = FunctionList.FuncList.get(nf).FuncIR.getNode(i);
		System.out.println(" ;"+currentIRNode.printNode());
                opcode = "";
                op1 = "";
                op2 = "";
                optype = "";

		//set the global IRNode too, so that it may be used by the functions that require it
		//(e.g. getRegister, newRegister, freeRegister
		currentNode = currentIRNode;
		if (currentIRNode.Predecessors.size() != 1) {
		    System.out.println(";Spilling registers at beginning of basic block");
		    spillAllRegisters();
		}

                if(currentIRNode.opcode.startsWith("STORE")) {
		    opcode = "move";

		    if(isLiteral(currentIRNode.op1)) {
			op1 = currentIRNode.op1;
			op2 = getSpecial(currentIRNode.res);
			TinyInstr instr = new TinyInstr(opcode, op1, op2);
			//System.out.println(instr.printInstr());
		    }
		    else if(currentIRNode.op1.startsWith("$T")) {
			op1 = getSpecial(currentIRNode.op1);
			op2 = getSpecial(currentIRNode.res);
			TinyInstr instr = new TinyInstr(opcode, op1, op2);
			//System.out.println(instr.printInstr());
		    }
		    else if(isSpecialNotTemp(currentIRNode.op1) && isSpecialNotTemp(currentIRNode.res)) {
			//e.g. STORE $L1 $R -> move $-1 r0; move r0 $8
			op1 = getSpecial(currentIRNode.op1);
			op2 = newRegister(op1);
			//op2 = newRegister();
			TinyInstr instr = new TinyInstr(opcode, op1, op2);
			System.out.println(instr.printInstr());
		                        
			op1 = op2;
			op2 = getSpecial(currentIRNode.res);
			instr = new TinyInstr(opcode, op1, op2);
			//System.out.println(instr.printInstr());
		    }
		    else {// if( ( op1.matches("([a-zA-Z_$][a-zA-Z\d_$]*)") ) )
			op1 = currentIRNode.op1;
			op2 = newRegister(op1);		
			TinyInstr instr = new TinyInstr(opcode, op1, op2);
			System.out.println(instr.printInstr());
		                        
			op1 = op2;
			op2 = currentIRNode.res;
			instr = new TinyInstr(opcode, op1, op2);	
		    }
		}
                else if(currentIRNode.opcode.startsWith("ADD") ||
                        currentIRNode.opcode.startsWith("SUB") ||
                        currentIRNode.opcode.startsWith("MULT") ||
                        currentIRNode.opcode.startsWith("DIV")) {
		    //Gets translated as 2 Tiny instructions
		    opcode = "move";
		    if (isSpecial(currentIRNode.op1)){
			op1 = getSpecial(currentIRNode.op1); }
		    else {
			op1 = currentIRNode.op1;}
		    op2 = getSpecial(currentIRNode.res);
                    
		    TinyInstr instr = new TinyInstr(opcode, op1, op2);
		    System.out.println(instr.printInstr());
                    
		    if (currentIRNode.opcode.endsWith("F")) {
			optype = "r"; }
		    else { optype = "i"; }
                    
		    if(currentIRNode.opcode.startsWith("ADD")) {
			opcode = "add"; }
		    else if (currentIRNode.opcode.startsWith("SUB")) {
			opcode = "sub"; }
		    else if (currentIRNode.opcode.startsWith("DIV")) {
			opcode = "div"; }
		    else if (currentIRNode.opcode.startsWith("MULT")) {
			opcode = "mul"; }
                    
		    opcode = opcode + optype;
		    if (isSpecial(currentIRNode.op2)){
			op1 = getSpecial(currentIRNode.op2); }
		    else {
			op1 = currentIRNode.op2; }
		    op2 = op2;
		}

                else if(currentIRNode.opcode.equals("WRITEI")){
                    opcode = "sys writei";
                    if (isSpecial(currentIRNode.op1)){
                        op1 = getSpecial(currentIRNode.op1); }
                    else {
                        op1 = currentIRNode.op1;}
                }
                else if(currentIRNode.opcode.equals("WRITEF")){
                    opcode = "sys writer";
                    if (isSpecial(currentIRNode.op1)){
                        op1 = getSpecial(currentIRNode.op1); }
                    else {
                        op1 = currentIRNode.op1;}
                }
                else if(currentIRNode.opcode.equals("WRITES")){
                    opcode = "sys writes";
                    if (isSpecial(currentIRNode.op1)){
                        op1 = getSpecial(currentIRNode.op1); }
                    else {
                        op1 = currentIRNode.op1;}
                }
                else if(currentIRNode.opcode.equals("READI")){
                    opcode = "sys readi";
                    if (isSpecial(currentIRNode.op1)){
                        op1 = getSpecial(currentIRNode.op1); }
                    else {
                        op1 = currentIRNode.op1;}
                }
                else if(currentIRNode.opcode.equals("READF")){
                    opcode = "sys readr";
                    if (isSpecial(currentIRNode.op1)){
                        op1 = getSpecial(currentIRNode.op1); }
                    else {
                        op1 = currentIRNode.op1;}
                }
                else if(currentIRNode.opcode.equals("READS")){
                    opcode = "sys reads";
                    if (isSpecial(currentIRNode.op1)){
                        op1 = getSpecial(currentIRNode.op1); }
                    else {
                        op1 = currentIRNode.op1;}
                }
                else if(currentIRNode.opcode.equals("GE") ||
                        currentIRNode.opcode.equals("LE") ||
                        currentIRNode.opcode.equals("GT") ||
                        currentIRNode.opcode.equals("LT") ||
                        currentIRNode.opcode.equals("EQ") ||
                        currentIRNode.opcode.equals("NE")
                        ){
                    //  ;;GE i $T4 label1
                    //becomes
                    //  cmpi i r3
                    //  jge label1
                    
                    //just need to check type of one operand to find type
                    //because LITTLE and Tiny do not work with operations involving
                    //different types
                    TinyInstr instr;
                    optype = getOpType(currentIRNode.op1);
                    if (optype.equals("")) { optype = "i"; } //hardcoding :)
                    opcode = "cmp"+optype;                    
             
                    if (isSpecial(currentIRNode.op1)){
                        op1 = getSpecial(currentIRNode.op1); }
                    else
			{
			    op1 = currentIRNode.op1;
			}
                    if (isSpecial(currentIRNode.op2)){
                        op2 = getSpecial(currentIRNode.op2);
                    }
                    else
			{ //it's a literal or a variablename
			    op2 = currentIRNode.op2;
			}
                    
                    //if op2 is a special but not temp
                    if(isSpecialNotTemp(currentIRNode.op2) ||
		       isIdentifier(currentIRNode.op2))
			{
			    op1 = getSpecial(currentIRNode.op2);
			    op2 = newRegister(op1);
			    instr = new TinyInstr("move", op1, op2);
			    System.out.println(instr.printInstr());
			    op1 = getSpecial(currentIRNode.op1);
			}
                    
                    //op2 = newRegister();
                    
                    instr = new TinyInstr(opcode, op1, op2);
                    System.out.println(instr.printInstr());

		    //if variable is not live after this instruction, free its register
		    if (!currentNode.InSet.contains(currentNode.op1)) {
			int reg = findRegister(currentNode.op1);
			if(reg != -1) { freeRegister(reg); }
		    }
		    if (!currentNode.InSet.contains(currentNode.op2)) {
			int reg = findRegister(currentNode.op2);
			if(reg != -1) { freeRegister(reg); }
		    }

                    opcode = "";
                    op1 = "";
                    op2 = "";
                    
                    opcode = "j"+currentIRNode.opcode.toLowerCase(); //converts GE to jge, LE to jle, etc
                    //		op1 = op2;
                    op1 = currentIRNode.res.toLowerCase(); //res stores the label for IRNodes
                }
                else if(currentIRNode.opcode.equals("JUMP")){
                    opcode = "jmp";
                    op1 = currentIRNode.op1.toLowerCase(); //not gonna happen but just to ensure it
                }
                else if(currentIRNode.opcode.equals("LABEL")){
                    opcode = "label";
                    if(!currentIRNode.op1.startsWith("label"))
			{
			    op1 = currentIRNode.op1;
			}
                    else{
                        op1 = currentIRNode.op1.toLowerCase();
                    }
                }
                else if(currentIRNode.opcode.equals("LINK")){
                    opcode = "link";
                    //get number of parameters
                    int temp = FunctionList.FuncList.get(nf).local_count;
		    temp += ValueRegister.registers.size(); //add in number of temporaries possible to be spilled
                    op1 = Integer.toString(temp);
                }
                else if(currentIRNode.opcode.equals("PUSH")){
                    opcode = "push";
                    if (!currentIRNode.op1.equals("")) {
                        op1 = getSpecial(currentIRNode.op1);
                    }
                }
                else if(currentIRNode.opcode.equals("POP")){
                    opcode = "pop";
                    if (!currentIRNode.op1.equals("")) {
                        op1 = getSpecial(currentIRNode.op1);
                    }
                }
                else if(currentIRNode.opcode.equals("JSR")){
                    op2 = "";
                    
                    //Save all registers
                    CallerSaves();
                    
                    opcode = "jsr";
                    op1 = currentIRNode.op1;
                    TinyInstr instr = new TinyInstr(opcode, op1, op2);
                    System.out.println(instr.printInstr());
                    
                    //Restore all registers
                    CallerRestores();
                    continue; //skip to next iteration because already printed
                }
                else if(currentIRNode.opcode.equals("RET")){
                    opcode = "unlnk";
                    TinyInstr instr = new TinyInstr(opcode, op1, op2);
                    System.out.println(instr.printInstr());
                    opcode = "ret";
                }

		//if variable is not live after this instruction, free its register
		if (!currentNode.InSet.contains(currentNode.op1)) {
		    int reg = findRegister(currentNode.op1);
		    if(reg != -1) { freeRegister(reg); }
		}
		if (!currentNode.InSet.contains(currentNode.op2)) {
		    int reg = findRegister(currentNode.op2);
		    if(reg != -1) { freeRegister(reg); }
		}
		if (!currentNode.InSet.contains(currentNode.res)) {
		    int reg = findRegister(currentNode.res);
		    if(reg != -1) { freeRegister(reg); }
		}

                TinyInstr instr = new TinyInstr(opcode, op1, op2);
                System.out.print(instr.printInstr());
		System.out.println("\t;"+printRegisters());
            }}
        TinyInstr instr = new TinyInstr("end", "", "");
        System.out.println(instr.printInstr());
    }
    
    public static void initializeIdentifiers() {
        SymbolTable.createTinyInstr();
	System.out.println("push");
    }
    public static void initializations() {
        CallerSaves();
        TinyInstr instr = new TinyInstr("jsr", "main", "");
        System.out.println(instr.printInstr());
        instr = new TinyInstr("sys halt", "", "");
        System.out.println(instr.printInstr());
	
	registerPool.add(new TinyRegister("r0"));
	registerPool.add(new TinyRegister("r1"));
	registerPool.add(new TinyRegister("r2"));
	registerPool.add(new TinyRegister("r3"));	
    }
    
    public static void CallerSaves() {
        int regn;
        String opcode = "push";
        String op2 = "";
        String op1;
        for (regn = 0; regn < 4; regn++) {
            op1 = "r"+Integer.toString(regn);
            TinyInstr instr = new TinyInstr(opcode, op1, op2);
            System.out.println(instr.printInstr());		    
        }
    }
    public static void CallerRestores() {
        int regn;
        String opcode = "pop";
        String op2 = "";
        String op1;
        for (regn = 3; regn >= 0; regn--) {
            op1 = "r"+Integer.toString(regn);
            TinyInstr instr = new TinyInstr(opcode, op1, op2);
            System.out.println(instr.printInstr());		    
        }
    }
    public static String getSpecial(String temp) {
        //converts a $Tx to rx
        //$x to $-x
        //$Px to $6+x
        //$R to $8
	//where $x is that item's location on the stack
        int par_count = FunctionList.FuncList.get(nfg).par_count;
	//    FunctionObject temp_func = FunctionList.FuncList.get(nfg);
	//      if(!temp_func.name.equals("GLOBAL"))
	//        {
	if (temp.startsWith("$T")) { //normal temporary to store into register
	    //Only going to perform register allocation on $T temporaries. Rest will use right off the stack.	    
	    return getRegister(temp);

	    //int n = Integer.parseInt(temp.substring(2));
	    //return "r"+Integer.toString(n-1); 
	}
	if (temp.startsWith("$L")) {
	    return "$-"+temp.substring(2);
	}
	if (temp.startsWith("$R")) {
	    return "$"+Integer.toString(6+par_count);}
	if (isParameter(temp)) {
	    paramcnt++;
	    int n = Integer.parseInt(temp.substring(2));
	    return "$"+Integer.toString(6+par_count-n);
	}
	return temp;
	// }
	//        else
	//        {
	//        	return 
	//        }
        //return registerPool.peek();
    }
    public static String getRegister(String var) {
	//basically ensure();
	//finds the variable in the registers. If the variable is not 
	//in the registers, it allocates a new register to it.
	TinyRegister r;
	int i;
	String return_reg;

	//search for the variable
	System.out.print(";Searching "+var+"..."); 
	for (i = 0; i < registerPool.size(); i++) {
	    if (registerPool.get(i).var.equals(var)) { System.out.println("found at "+i); break; }
	}

	//could not find the variable
	if (i >= registerPool.size()) {
	    System.out.println("couldnotfind");
	    return_reg = newRegister(var);
	}
	//found the variable!
	else { 
	    return_reg = registerPool.get(i).number;
	}

	return return_reg;
    }
    public static String newRegister(String var) {
	//returns the next free register. basically allocate().
	//if there is no free register, will have to spill one. Spills one at random.
	TinyRegister r; //initialize to first one
	int i;
	for (i = 0; i < registerPool.size(); i++) {
	    //search for an empty register
	    if (registerPool.get(i).var.equals("")) { break; }
	}
	if (i >= registerPool.size()) { //could not find an empty reg
	    //always spills the first register (r0).
	    r = freeRegister(0);
	}
	else {
	    r = registerPool.get(i);
	}
	r.var = var;
	r.dirty = true;
	return r.number;
	
        //registerPool.addFirst("r"+Integer.toString(registercount++));
        //return registerPool.peek();
    }
    public static TinyRegister freeRegister(int reg) {
	TinyRegister r = registerPool.get(reg);
	
//	if (currentNode.InSet.contains(var) && //if variable is still live after this instruction,

	if (r.dirty) {
	    //if it is dirty, will have to store it onto stack
	    //    TinyInstr instr = new TinyInstr("move", r.number, r.var);
	    //    System.out.println(instr.printInstr()+"  ;spilled");
	    //^------------- don't need to do this check since we are only using regs for temporaries and they dont change
	    //we could have (should have) used regs for everything else as well but Tiny works swell either way
	}
	r.var = ""; //mark this reg as free
	r.dirty = false;
	return r;
    }
    public static int findRegister(String var) {
	int i;
	for (i = 0; i < registerPool.size(); i++) {
	    if (registerPool.get(i).var == var) {
		return i;
	    }
	}//else:
	return -1;
    }
    public static void spillAllRegisters() {
	freeRegister(0);
	freeRegister(1);
	freeRegister(2);
	freeRegister(3);
    }
    /*
    public static void freeRegister(String reg, String var) {
	//free a given register by force
	TinyRegister temp = freeRegister(Integer.parseInt(reg.substring(1)), var);
	}*/
    public static String printRegisters() {
	String ret = "";
	int i; TinyRegister r;
	ret = ret+"{";
	for (i = 0; i < registerPool.size(); i++) {
	    ret = ret+" "+registerPool.get(i).number+":";
	    ret = ret+""+registerPool.get(i).var;
	}
	ret = ret+" }";
	return ret;
    }
    public static String getOpType(String op) {
        if (op.startsWith("$T")) { //if it is a register,
            //so find register type
            //	    System.out.print("Register "+op1);
            return ValueRegister.getType(op).toLowerCase();
        }
        else if (op.matches("[0-9]+")) { //if it's an int
            return "i";
        }
        else if (op.contains(".")) { //if it's a float
            return "r";
        }
        else { //it is a symbol/literal
            //so find symbol type	    
            String retval = SymbolTable.getType(op).toLowerCase();
            if (retval.startsWith("f")) { return "r"; } //it's a float type
            else { return retval; }
        }
    }
    public static Boolean isIdentifier(String id) {
        if (id.matches("[0-9]+.*")) {
            return false; }
        else if (id.startsWith("$")) {
            return false; }
        else if (id.matches("([a-zA-Z])+([0-9])*([a-zA-Z])*")) {
            return true;
        } else { return false; }
    }
    public static Boolean isSpecial(String op) {
        //CHECKS IF IT'S A SPECIAL, E.G. $T, $R, $P, $L
        if (op.startsWith("$")) {
            return true;
        } else { return false; }
    }
    public static Boolean isReturnValue(String id) {
        if (id.startsWith("$L")) {
            return true;
        } else { return false; }
    }
    public static Boolean isParameter(String id) {
        if (id.startsWith("$P")) {
            return true;
        } else { return false; }
    }
    public static Boolean isSpecialNotTemp(String op) {
        //CHECKS IF IT'S A SPECIAL, E.G. $R, $P, $L
        if (op.startsWith("$") && !op.startsWith("$T")) {
            return true;
        } else { return false; }
    }
    
    public static Boolean isLiteral(String id) {
        if (id.matches("[0-9]+(.[0-9]+)?"))
	    {
		return true;
	    }
        else return false;
    }
}