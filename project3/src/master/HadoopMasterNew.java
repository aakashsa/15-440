package master;


public class HadoopMasterNew {

	public static void main(String[] args) {
		System.out.println(" Master Started Lets Start Scanning : ");
		System.out.println("  Scanning : ");		
		new Thread(new Scan()).start();		
	}
}
