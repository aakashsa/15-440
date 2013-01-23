
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class processManager {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (!args[0].equals("processManager")){
			System.out.println("Invalid Command");
		}
		if (args[1].equals("-c")){
			// Slave Case
		}
		else {
			//Master Case
		}
			
			
			
	}	
}
 /* 
	
	// TODO Auto-generated method stub
	try {
		Class c = Class.forName("processes.GrepProcess");
		System.out.println(" Class Name = " +c.getName() + " \n");

		Class[] ctorArgs1 = new Class[1];

		ctorArgs1[0] = String[].class;
        Constructor strCtor = null;
		try {
			strCtor = c.getConstructor(ctorArgs1);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
            System.out.println("Constructor found: " +
                strCtor.toString() +"\n");
            String [] hello = new String[3];
            hello[0] = "hewyy";
            hello[1] = "hdhaj";
            hello[2] =" yfabjdbsja";
            try {
				System.out.println(" New Instance = " + strCtor.newInstance((Object) hello).toString() );
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
	*/