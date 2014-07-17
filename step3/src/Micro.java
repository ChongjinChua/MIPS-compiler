import org.antlr.v4.runtime.*;
import java.util.*;
import java.io.*;

public class Micro {
    public static void main(String argv[]) throws Exception {
	MicroGrammarLexer lexerObj = new MicroGrammarLexer(new ANTLRFileStream(argv[0]));
	CommonTokenStream tokenObj = new CommonTokenStream(lexerObj);
	MicroGrammarParser parserObj = new MicroGrammarParser(tokenObj);

	//ErrorHandle handler = new ErrorHandle();
	parserObj.setErrorHandler(new ErrorHandle());
	
	parserObj.program();
	//	System.out.println("Accepted");
	
	/*	
	//STEP 1 CODE:
	Token tok = lexerObj.nextToken();
	int ttype=0;

	HashMap<String, String> mMap = new HashMap<String,String>();

	BufferedReader br = new BufferedReader(new FileReader("../build/MicroGrammar.tokens"));
	String line = br.readLine();
	String[] stArray;
	while(line!=null) {
	    stArray = line.split("=");
	    mMap.put(stArray[1],stArray[0]);
	    line = br.readLine();
	    //System.out.println("Entry:"+stArray[0]+"\t"+"Key:"+stArray[1]);
	}

	String stype;
	String Type;

	while(tok.getType() != -1) {
	    ttype = lexerObj.getType();
	    stype = Integer.toString(ttype);
	    Type = mMap.get(stype);
	    System.out.println("Token Type: "+Type);
	    System.out.println("Value: "+tok.getText());
	    tok = lexerObj.nextToken();
	}
	br.close();*/
	}
}

class ErrorHandle extends DefaultErrorStrategy
{
    public void reportError(Parser recognizer, RecognitionException e)
    {
	System.out.println("Not Accepted");
	System.exit(1);
    }
}

