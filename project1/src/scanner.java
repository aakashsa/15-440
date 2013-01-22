
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class scanner {

	/**
	 * @param args
	 */
		    public static void main(String[] args) throws IOException {
		    	
	        	System.out.print("\n Reading Lines \n");

	            ArrayList<String> myArr = new ArrayList<String>();
	            int i =0;
	        	Scanner sc2 = null;
		    	sc2 = new Scanner(System.in);
		    	while (sc2.hasNextLine()) {
		    	
	    	    	System.out.print("\n Line  : " +i +"\n");

		    	    Scanner s2 = new Scanner(sc2.nextLine());
		    	    boolean b;
		    	    while (b = s2.hasNext()) {
		    	    	System.out.print("\n Read Word :  \n");
		    	        String s = s2.next();
		    	        myArr.add(s);
		            	System.out.print(myArr.get(myArr.size()-1));
		    	    }
		    	    i++;
		    	}
		    }
}
		


	
