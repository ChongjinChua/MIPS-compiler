import java.io.*;
import java.util.*;

public class Symbol {
    private String name;
    private String idtype;
    private String value;

    public Symbol(String name, String idtype, String value) {
	this.name = name;
	this.idtype = idtype;
	this.value = value;
	//	this.value = Integer.toString(value);
    }

    public Symbol(String name, String idtype) {
	this.name = name;
	this.idtype = idtype;
	this.value = null;
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
    public void setValue(String val) {
	this.value = val;
    }
    public void printSymbol() {
	if (value == "") {
	    System.out.println("name "+name+" type "+idtype);
	}
	else {
	    System.out.println("name "+name+" type "+idtype+" value "+value);
	}
    }
}