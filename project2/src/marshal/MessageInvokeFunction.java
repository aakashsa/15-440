package marshal;

import java.io.Serializable;
import java.util.Arrays;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

public class MessageInvokeFunction implements Serializable {

	private Class[] types;
	private Object[] args;
	private String funName;
	private Object returnVal;
	private Exception exp;
	private int objectKey;
	private String objName;

	public MessageInvokeFunction(String funName, Object[] args, Class[] types,
			Object returnVal, Exception exp, int objectKey, String objName) {
		this.funName = funName;
		this.args = args;
		this.types = types;
		this.returnVal = returnVal;
		this.exp = exp;
		this.objectKey = objectKey;
		this.objName = objName;
	}

	public int getObjectKey() {
		return objectKey;
	}

	public String getFunctionName() {
		return funName;
	}

	public String getObjName() {
		return objName;
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
