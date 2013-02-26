package registryROR;

public class RemoteBarImpl implements RemoteBar{

	@Override
	public int getbar() {
		System.out.println(" Called GETBAR IN REMOTE BAR");
		return 10;
	}
	
}
