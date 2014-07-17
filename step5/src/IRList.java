import java.util.*;
import java.io.*;

public class IRList{
    
    public static List<IRNode> IRL = new ArrayList<IRNode>();;
    
//    public static void IRList()
//    {
//       IRL = new ArrayList<IRNode>();
//    }
    
    public static void AddNode(IRNode node)
    {
        if(node != null)
        {
            IRL.add(node);
        }
    }
    
    public static void PrintList()
    {
        int i;
        System.out.println(";IR code");
        for(i=0;i<IRL.size();i++)
        {
            System.out.println(";"+IRL.get(i).printNode());
        }
    }
    
    public static IRNode getNode(int i)
    {
        return IRL.get(i);
    }
    
    public static int getSize()
    {
        return IRL.size();
    }

}