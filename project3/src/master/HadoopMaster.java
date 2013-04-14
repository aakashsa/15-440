package master;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import lib.ConstantsParser;
import lib.Job;

import communication.ChunkObject;
import communication.WorkerInfo;

/**
 * Hadoop master. Upon startup it parses the config file and 
 * spawns a thread to start scanning for jobs
 */
public class HadoopMaster {

	/**
	 * A lock for operations on queues and maps
	 */
	public static final Object QUEUE_LOCK = new Object();	
	/**
	 * Initialize a counter for counting jobs
	 */
	public static int jobCounter = 0;
	/**
	 * Free workers queue
	 */
	public static ConcurrentLinkedQueue<Integer> freeWorkers;
	/**
	 *  Map from workers to chunks 
	 */
	public static ConcurrentHashMap<Integer, ChunkObject> busyWorkerMap;
	/**
	 *  Map from job ID to JobThread Object
	 */
	public static ConcurrentHashMap<Integer, JobThread> jobThreadObjectMap;
	/**
	 *  Map from job ID to the actual Thread object running the Job
	 */
	public static ConcurrentHashMap<Integer, Thread> jobThreadMap;
	/**
	 * Map from worker number to its WorkerInfo
	 */
	public static ConcurrentHashMap<Integer, WorkerInfo> allWorkers;
	/**
	 * Job ID to Job mapping
	 */
	public static ConcurrentHashMap<Integer, Job> jobMap;
	/**
	 * Record size (in bytes)
	 */
	public static long recordSize;
	/**
	 * Chunk size (in bytes)
	 */
	public static long chunkSize;
	/**
	 * Number of workers
	 */
	public static int numWorkers;
	/**
	 * Number of reducers
	 */
	public static long numReducers;
	/**
	 * Instance of constants parser
	 */
	public static ConstantsParser cp;

	public static void main(String[] args) {
		// Initialize data structures
		freeWorkers = new ConcurrentLinkedQueue<Integer>();
		busyWorkerMap = new ConcurrentHashMap<Integer, ChunkObject>();
		jobMap = new ConcurrentHashMap<Integer, Job>();
		jobThreadMap = new ConcurrentHashMap<Integer, Thread>();;
		jobThreadObjectMap = new ConcurrentHashMap<Integer, JobThread>();

		// Parse the JSON config file
		if (args.length != 1) {
			System.out.println("Usage: HadoopMaster <configFilePath>");
			System.exit(-1);
		}
		
		cp = new ConstantsParser(args[0]);
		recordSize = cp.getRecordSize();
		chunkSize = cp.getChunkSize();
		allWorkers = cp.getAllWorkers();
		numWorkers = allWorkers.size();
		numReducers = cp.getNumReducers();
		
		// Add all workers to free workers queue
		for (int i = 0; i < HadoopMaster.numWorkers; i++) {
			HadoopMaster.freeWorkers.add(i);
		}
		System.out.println("Master ready...\n");
		new Thread(new Scan()).start();
	}
}
