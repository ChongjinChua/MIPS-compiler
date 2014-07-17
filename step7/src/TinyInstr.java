import java.util.*;
import java.io.*;

public class TinyInstr
{
    public String opcode;
    public String op1;
    public String op2;

    public TinyInstr(String opcode, String op1, String op2)
    {
        this.opcode = opcode;
        this.op1 = op1;
        this.op2 = op2;
    }

    public String printInstr()
    {
        String msg = opcode+" "+op1+" "+op2;
	msg = msg.trim();
	return msg;
    }

}