package rmi440.tests.common;

import java.util.ArrayList;

import rmi440.commoncode.Remote440;

/**
 * Interface for sample remote object
 */
public interface RemoteBar extends Remote440 {

	int getRandomSize(ArrayList<Integer> a);
	RemoteBar renew();
	void changeState();
	int getState();
}
