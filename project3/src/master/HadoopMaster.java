package master;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import communication.ChunkObject;
import communication.ReduceTask;
import communication.ServiceMapThread;
import communication.MapTask;
import communication.WorkerInfo;

import lib.ConstantsParser;
import lib.Job;
import lib.Utils;

import java.util.HashMap;
import java.util.concurrent.*;

import test.JobConfiguration;

public class HadoopMaster {

	public static Socket[] workerSockets;

	public static ConcurrentLinkedQueue<ChunkObject> chunkQueue;
	public static ConcurrentLinkedQueue<Integer> freeWorkers;

	public static ConcurrentHashMap<ChunkObject, Integer> chunkWorkerMap;
	public static ConcurrentHashMap<Integer, ChunkObject> busyWorkerMap;

	public static final Object OBJ_LOCK = new Object();

	public static int fileSizeRead = 0;

	public static int mapsDone = 0;
	
	public static void main(String[] args) {

		// Initialize status data structures
		chunkQueue = new ConcurrentLinkedQueue<ChunkObject>();
		freeWorkers = new ConcurrentLinkedQueue<Integer>();
		chunkWorkerMap = new ConcurrentHashMap<ChunkObject, Integer>();
		busyWorkerMap = new ConcurrentHashMap<Integer, ChunkObject>();

		String inputFile = args[0];

		// Parse the JSON config file
		ConstantsParser cp = new ConstantsParser();
		long recordSize = cp.getRecordSize();
		long chunkSize = cp.getChunkSize();
		HashMap<Integer, WorkerInfo> allWorkers = cp.getAllWorkers();
		int numWorkers = allWorkers.size();

		// Get jobs and do sanity checks
		String jobConfClassName = "test.JobConfiguration";
		Job job = null;
		try {
			Class<?> jobConfClass = Class.forName(jobConfClassName);
			JobConfiguration jConf = (JobConfiguration) jobConfClass.newInstance();
			Job[] jobs = jConf.setup();
			job = jobs[0];
			Utils.performJobSanityChecks(job);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
		
		// Get input file name and size
		System.out.println("File Path = " + inputFile);
		File f = new File(inputFile);
		int fileSize = (int) f.length();
		System.out.println("File Size = " + fileSize);

		long round = (fileSize % recordSize);
		long numRecordsInFile = (fileSize / recordSize);
		if (round != 0)
			numRecordsInFile++;
		long numRecordsPerChunk = chunkSize / recordSize;

		System.out.println(" num of Records in File = " + numRecordsInFile);
		System.out.println(" num of Records per Chunk = " + numRecordsPerChunk);

		long numChunks = fileSize / (numRecordsPerChunk * recordSize);
		round = fileSize % (numRecordsPerChunk * recordSize);

		if (round != 0)
			numChunks++;
		System.out.println(" num of Chunks = " + numChunks);

		// Spawn threads for telling workers to map appropriate chunks
		// First add all workers to free workers queue
		workerSockets = new Socket[(int) numWorkers];
		for (int i = 0; i < numWorkers; i++) {
			freeWorkers.add(i);
		}

		// Add all chunks to chunk queue, and assign to null workers initially
		for (int i = 0; i < numChunks; i++) {
			ChunkObject chunKey = new ChunkObject(i, i * numRecordsPerChunk,
					numRecordsPerChunk, (int) recordSize, inputFile);
			chunkQueue.add(chunKey);
			chunkWorkerMap.put(chunKey, -1);
		}
		
		Thread[] t_array = new Thread[(int) numChunks];
		int i = 0;
		
		// Mapping 
		while (!chunkWorkerMap.isEmpty() && !chunkQueue.isEmpty()) {
			synchronized (OBJ_LOCK) {
				if (!freeWorkers.isEmpty() && !chunkQueue.isEmpty()) {
					ChunkObject chunkJob = null;
					int newWorker = 0;
					chunkJob = chunkQueue.remove();
					MapTask task = new MapTask(chunkJob, job.getFileInputFormatClass(), job.getMapperClass());
					newWorker = freeWorkers.remove();
					busyWorkerMap.put(newWorker, chunkJob);
					t_array[i] = new Thread(new ServiceMapThread(task, newWorker,
							allWorkers.get(newWorker)));
					t_array[i].start();
					i++;
				}
			}
		}
		
		System.out.println("[INFO] Done mapping. Starting reduce tasks...");
		
		Socket reduceSocket;
		OutputStream output;
		ObjectOutputStream out;
		
		while (true) {
			if (HadoopMaster.mapsDone == numChunks) {
				System.out.println(" SENDING REDUCE COMMANDS");
				for (WorkerInfo info : allWorkers.values()) {
					try {
						reduceSocket = new Socket(info.getHost(), info.getPort());
						output = reduceSocket.getOutputStream();
						out = new ObjectOutputStream(output);
						out.flush();
						ReduceTask task = new ReduceTask(info.getWorkerNum(), job.getReducerClass(), job.getMapperOutputKeyClass(), job.getMapperOutputValueClass(), "final_answers/");
						out.writeObject(task);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				break;
			}
			System.out.println(" mapsdone = " + HadoopMaster.mapsDone);
		}
	}
}
