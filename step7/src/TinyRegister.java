import java.util.*;
public class TinyRegister {
    String number; //r0, r1, r2, or r3
    String var; //name of the variable it is holding
    Boolean dirty;

    public TinyRegister(String num) {
	this.number = num;
	this.var = "";
	this.dirty = false;
    }
}