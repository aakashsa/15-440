package marshal;

import java.io.Serializable;
import java.util.Arrays;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

public class MessageInvokeFunction implements Serializable {

	private Class[] types;
	private Object[] args;
	private String name;
	private Object returnVal;
	private Exception exp;
	private int objectKey;
	
	public MessageInvokeFunction(String name, Object[] args, Class[] types,
			Object returnVal, Exception exp, int objectKey) {
		this.name = name;
		this.args = args;
		this.types = types;
		this.returnVal = returnVal;
		this.exp = exp;
		this.objectKey = objectKey;
	}
	
	public int getObjectKey (){
		return objectKey;
	}

	public String getFunctionName() {
		return name;
	}

	public Object[] getArgs() {
		return args;
	}

	public Class[] getTypes() {
		return types;
	}

	public Object getRetVal() {
		return returnVal;
	}

	public Exception getExp() {
		return exp;
	}
}
