import java.util.*;
import java.io.*;

public class IRList{
    
    public List<IRNode> IRL = new ArrayList<IRNode>();
//    public static void IRList()
//    {
//       IRL = new ArrayList<IRNode>();
//    }
    
    public void AddNode(IRNode node)
    {
        //System.out.println("IN IR Add");
        if(node != null)
        {
            IRL.add(node);
        }
    }
    
    public void PrintList()
    {
        int i;
        //System.out.println(";IR code");
        for(i=0;i<IRL.size();i++)
        {
        System.out.print(";");
        IRL.get(i).printOutSet();
	System.out.println();
            System.out.print(";"+IRL.get(i).printNode().trim()+"\t");
        IRL.get(i).printPredSucc();
        System.out.print(";");
	IRL.get(i).printInSet();
        System.out.println(); 
        System.out.println("; |");    
        }
	//System.out.println(""); //extra dividing line between function scopes
    }

    public IRNode ListTop()
    {
        return IRL.get(IRL.size()-1);
    }
    
    public IRNode getNode(int i)
    {
        return IRL.get(i);
    }
    
    public int getSize()
    {
        return IRL.size();
    }

}