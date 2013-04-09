package master;

import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import lib.ConstantsParser;

import communication.ChunkObject;
import communication.WorkerInfo;

public class HadoopMaster {


	public static final Object QUEUE_LOCK = new Object();

	public static ConcurrentLinkedQueue<Integer> freeWorkers;
	public static ConcurrentHashMap<Integer, ChunkObject> busyWorkerMap;
	public static ConcurrentHashMap<Integer, WorkerInfo> allWorkers;

	public static long recordSize;
	public static long chunkSize;
	public static int numWorkers;
	public static long numReducers;
	public static ConstantsParser cp;

	public static void main(String[] args) {
		freeWorkers = new ConcurrentLinkedQueue<Integer>();
		busyWorkerMap = new ConcurrentHashMap<Integer, ChunkObject>();

		// Parse the JSON config file
		if (args.length < 1)
			throw new IllegalArgumentException("Specify Config File eg. test/Constants.json");
		cp = new ConstantsParser(args[0]);
		recordSize = cp.getRecordSize();
		chunkSize = cp.getChunkSize();
		allWorkers = cp.getAllWorkers();
		numWorkers = allWorkers.size();
		numReducers = cp.getNumReducers();
		// Spawn threads for telling workers to map appropriate chunks
		// First add all workers to free workers queue
		for (int i = 0; i < HadoopMaster.numWorkers; i++) {
			HadoopMaster.freeWorkers.add(i);
		}
		System.out.println("Master ready\n");
		new Thread(new Scan()).start();
	}
}
