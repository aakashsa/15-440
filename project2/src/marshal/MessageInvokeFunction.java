package marshal;

import java.io.Serializable;

/**
 * This class encapsulates a message for invoking a method
 * on a remote object
 */
public class MessageInvokeFunction implements Serializable {

	private static final long serialVersionUID = 3591693051047559786L;
	
	// Store all attributes needed to call a method on a remote object
	
	// Need argument objects and their types
	private Class<?>[] types;
	private Object[] args;
	
	// Function name, return value, and exception
	private String funName;
	private Object returnVal;
	private Exception exp;
	private Class<?> returnType;
	
	private int objectKey;
	// Object name as binded in registry
	private String objName;

	// Constructor - just saves all attributes to get later
	public MessageInvokeFunction(String funName, Object[] args, Class<?>[] types,
			Object returnVal, Exception exp, int objectKey, String objName, Class<?> returnType) {
		this.funName = funName;
		this.args = args;
		this.types = types;
		this.returnVal = returnVal;
		this.exp = exp;
		this.objectKey = objectKey;
		this.objName = objName;
		this.returnType = returnType;
	}

	// Getters for all fields
	
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

	public Class<?>[] getTypes() {
		return types;
	}

	public Object getRetVal() {
		return returnVal;
	}

	public Exception getExp() {
		return exp;
	}	
	
	public Class<?> getReturnType() {
		return returnType;
	}
}
