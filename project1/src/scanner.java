
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class scanner {

	/**
	 * @param args
	 */
		    public static void main(String[] args) throws IOException {

		        Scanner s = null;

		        try {
		        	System.out.print("\n Reading Lines \n");
			           
		            s = new Scanner(System.in);
		            	
		            ArrayList<String> myArr = new ArrayList<String>();
		            
		            while (s.hasNextLine()) {
		            	
		            	System.out.print("\n Read Word :  \n");
		            	myArr.add(s.next());
		            	System.out.print(myArr.get(myArr.size()-1));
		            }
		            
		            
		        } finally {
		            if (s != null) {
		                s.close();
		            }
		        }
		    }
}
		
	


