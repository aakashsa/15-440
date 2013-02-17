package marshal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

public class testMarshall {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		testObject test1 = new testObject("Object 1");
		System.out.println(" Calling method before Marshalling Normally");
		test1.SayHello("SHeila ", "Munni");
		System.out.println(" Marshalling");
		Object [] arg1 = new String[]{"SHeila ", "Munni"};
		Class [] arg2 = new Class[]{ String.class , String.class };
		
		MessageInvokeFunction marshal = new MessageInvokeFunction("SayHello", arg1, arg2);
			
	    try {
	    	System.out.println("Unmarshalling object now.");
	    	System.out.println("Arg1 = "+marshal.getFunctionName());
	    	System.out.println("Arg2 = "+marshal.getTypes());
	    	
			Method methodInvoke = test1.getClass().getDeclaredMethod(marshal.getFunctionName() ,marshal.getTypes() );
			System.out.println("Method Invoke = " + methodInvoke.toString());
		    System.out.println("Args = " + marshal.getArgs());
//		    methodInvoke.invoke(arg0, arg1)
			methodInvoke.invoke(test1, marshal.getArgs());
		    System.out.println("After Invoking the method");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	}

}
