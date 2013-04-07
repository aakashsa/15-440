package master;


public class HadoopMaster {

	public static void main(String[] args) {
		System.out.println("Master ready\n");
		new Thread(new Scan()).start();		
	}
}
