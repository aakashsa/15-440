package registryROR;

import java.util.ArrayList;

/**
 * Interface for sample remote object
 */
public interface RemoteBar extends Remote440 {

	int getbar();
	int barRem(int b);
	int getRandomSize(ArrayList<Integer> a);
	RemoteBar renew();
	void changeState();
	int getState();
}
