package marshal;

import java.io.Serializable;
import java.util.Arrays;

public class InvokeFunction implements Serializable {
	
	private String functionN;
	private Object []args;
	private Class [] types;
	public InvokeFunction(String functionName, Object [] args,Class [] types){
		this.functionN = functionName;
		this.args = args;
		this.types = types;
	}		
	public String getFunctionName(){
		return functionN;
	}
	
	public Object[] getArgs(){
		return args;
	}
	public Class[] getTypes(){
		return types;
	}
}
