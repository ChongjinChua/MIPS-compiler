import java.util.*;
import java.io.*;

public class IRNode {
    public String opcode;
    public String op1;
    public String op2;
    public String res;    
    public IRNode target;

    public IRNode(String opcode, String op1, String op2, String res) {
	this.opcode = opcode;
	this.op1 = op1;
	this.op2 = op2;
	this.res = res;
    }
    
    public String printNode() {
	if (op2.equals("")) {
	    return opcode+" "+op1+" "+res;
	}
	else {
	    return opcode+" "+op1+" "+op2+" "+res;
	}
    }
}