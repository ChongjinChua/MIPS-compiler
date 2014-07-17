import java.util.*;
import java.io.*;

public class IRNode {
    public String opcode;
    public String op1;
    public String op2;
    public String res;    
    public IRNode target;
    
    //step 7 (Liveness Analysis) structures
    Boolean alreadyVisited;
    public HashSet<IRNode> Predecessors = new LinkedHashSet<IRNode>(); //predecessor IR Nodes
    public HashSet<IRNode> Successors = new LinkedHashSet<IRNode>(); //successor IR Nodes
    public HashSet<String> OutSet = new LinkedHashSet<String>();
    public HashSet<String> InSet = new LinkedHashSet<String>();
    //public HashSet<String> LiveSet = new LinkedHashSet<String>();

    public Map<String,String> in;
    public Map<String,String> out;
    public Map<String,String> gen;
    public Map<String,String> kill;
    //
    
    public IRNode(String opcode, String op1, String op2, String res)
    {
        this.opcode = opcode;
        this.op1 = op1;
        this.op2 = op2;
        this.res = res;
        
        InSet.clear(); //initialize to 0 elements
	OutSet.clear();
        this.alreadyVisited = false;

        /*
        predecessors = new ArrayList<IRNode>();
        successors = new ArrayList<IRNode>();
        in = new HashMap<String,String>();
        out = new HashMap<String,String>();
        gen = new HashMap<String,String>();
        kill = new HashMap<String,String>();
        */
        
    }
    
    public String printNode() {
	if (op2.equals("")) {
	    return opcode+" "+op1+" "+res;
	}
	else {
	    return opcode+" "+op1+" "+op2+" "+res;
	}
    }
    
    public void addSucc(IRNode succ_node) {
    Successors.add(succ_node);
    }

    public void addPred(IRNode pred_node) {
    Predecessors.add(pred_node);
    }
    
    public void clearPred() {
    Predecessors.clear();
    }

    public void printInSet() {
    Iterator<String> iter = InSet.iterator();
    System.out.print(" ^in{");
    while(iter.hasNext()) {
        String i = iter.next();
        System.out.print(i+" ");
    }
    System.out.print("}");
    }

    public void printOutSet() {
    Iterator<String> iter = OutSet.iterator();
    System.out.print(" ^out{");
    while(iter.hasNext()) {
        String i = iter.next();
        System.out.print(i+" ");
    }
    System.out.print("}");
    }

    public void printPredSucc() {
    Iterator<IRNode> iterp = Predecessors.iterator();
    System.out.print("  {PRED:");
    while(iterp.hasNext()) {
        IRNode node = iterp.next();
        System.out.print(" "+node.printNode());
    }
    System.out.print("} ");

    Iterator<IRNode> iters = Successors.iterator();
    System.out.print("{SUCC:");
    while(iters.hasNext()) {
        IRNode node = iters.next();
        System.out.print(" "+node.printNode());
    }
    System.out.println("}");
    }
}