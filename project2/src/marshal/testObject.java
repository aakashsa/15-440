package marshal;

public class testObject {

	private String objectName;	
	public testObject(String objectName) {
		this.objectName = objectName;
		// TODO Auto-generated constructor stub
	}
	
	public String SayHello(String name1,String name2){
		System.out.println("WHATS UP CHUT ! "+ name1 + name2 + objectName);
		return "WHATS UP CHUT ! "+ name1 + name2;
	}
}
