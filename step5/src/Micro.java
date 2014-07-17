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

