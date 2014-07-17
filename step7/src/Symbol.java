import java.io.*;
import java.util.*;

public class Symbol {
    String name;
    String idtype;
    String value;
    String reg;
    
    public Symbol(String name, String idtype, String value, String reg) {
	this.name = name;
	this.idtype = idtype;
	this.value = value;
        this.reg = reg;
	//	this.value = Integer.toString(value);
    }

    public Symbol(String name, String idtype) {
	this.name = name;
	this.idtype = idtype;
	this.value = null;
        this.reg = null;
    }

    public String getName() {
	return this.name;
    }
    public String getType() {
	return this.idtype;
    }
    public String getValue() {
	return this.value;
    }
    public String getReg() {
        return this.reg;
    }
    
    public void setValue(String val) {
	this.value = val;
    }
    
    public void printSymbol() {
	if (value == "") {
	    System.out.println("name "+name+" type "+idtype);
	}
	else if(reg == ""){
	    System.out.println("name "+name+" type "+idtype+" value "+value);
	}
    else{
        System.out.println("name "+name+" type "+idtype+" value "+value+" reg "+reg);
    }
    
    }
}