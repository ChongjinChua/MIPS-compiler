import java.io.*;
import java.util.*;
import java.lang.*;

public class FunctionList{

    static List<FunctionObject> FuncList = new ArrayList<FunctionObject>();
    static FunctionObject top;
    
//    FunctionObject{
//        top = ListPeek();
//    }
    
    public static FunctionObject ListPeek(){
        if(FuncList.size()!=0)
        {
           return FuncList.get(FuncList.size()-1);
        }
        return null;
    }

    public static FunctionObject PeekGlobal(){
        if(!FuncList.isEmpty())
        {
	    return FuncList.get(0);
        }
        return null;
    }
    public static FunctionObject PeekHigher(int dist){
	//not used anymore
        if(FuncList.size()>=2)
        {
           return FuncList.get(FuncList.size()-2-dist);
        }
        return null;
    }
    
    public static void PrintFunctions()
    {
        FunctionObject temp;
        System.out.println(";IR code");
        for(int i = 0;i<FuncList.size();i++)
        {
            temp = FuncList.get(i);
            temp.FuncIR.PrintList();
           // System.out.println("\n");
        }
    }
}