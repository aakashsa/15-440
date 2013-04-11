package master;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import lib.ConstantsParser;
import lib.Job;

import communication.ChunkObject;
import communication.WorkerInfo;

public class HadoopMaster {


	public static final Object QUEUE_LOCK = new Object();
	
	// Initialize status data structures
	public static int counter = 0;
	// Free Worker Queue
	public static ConcurrentLinkedQueue<Integer> freeWorkers;
	// Maps Workers to Chunks 
	public static ConcurrentHashMap<Integer, ChunkObject> busyWorkerMap;

	// Maps worker number to its WorkerInfo
	public static ConcurrentHashMap<Integer, WorkerInfo> allWorkers;
	
	// Job Id to Job Mapping
	public static ConcurrentHashMap<Integer, Job> jobMap;

	public static long recordSize;
	public static long chunkSize;
	public static int numWorkers;
	public static long numReducers;
	public static ConstantsParser cp;

	public static void main(String[] args) {
		freeWorkers = new ConcurrentLinkedQueue<Integer>();
		busyWorkerMap = new ConcurrentHashMap<Integer, ChunkObject>();
		jobMap = new ConcurrentHashMap<Integer, Job>();
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
