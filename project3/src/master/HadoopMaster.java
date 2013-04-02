package master;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import communication.ChunkObject;
import communication.ServiceThread;
import communication.WorkerInfo;

import lib.Constants;
import lib.ConstantsParser;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class HadoopMaster {

	public static Socket[] workerSockets;

	public static ConcurrentLinkedQueue<ChunkObject> chunkQueue;
	public static ConcurrentLinkedQueue<Integer> freeWorkers;

	public static ConcurrentHashMap<ChunkObject, Integer> chunkWorkerMap;
	public static ConcurrentHashMap<Integer, ChunkObject> busyWorkerMap;

	public static final Object OBJ_LOCK = new Object();

	public static int fileSizeRead = 0;

	/*
	 * * @param args
	 */
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
		long numMappers = cp.getNumMappers();
		long numReducers = cp.getNumReducers();
		String fileInputFormat = cp.getInputFormat();
		HashMap<Integer, WorkerInfo> allWorkers = cp.getAllWorkers();
		int numWorkers = allWorkers.size();
		
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
			ChunkObject chunKey = new ChunkObject(i, i * numRecordsPerChunk, numRecordsPerChunk, (int) recordSize, inputFile);
			chunkQueue.add(chunKey);
			chunkWorkerMap.put(chunKey, -1);
		}
		while (!chunkWorkerMap.isEmpty() && !chunkQueue.isEmpty()) {
			synchronized (OBJ_LOCK) {
				if (!freeWorkers.isEmpty() && !chunkQueue.isEmpty()) {
					ChunkObject chunkJob = null;
					int newWorker = 0;
					chunkJob = chunkQueue.remove();
					newWorker = freeWorkers.remove();
					busyWorkerMap.put(newWorker, chunkJob);
					new Thread(new ServiceThread(chunkJob, newWorker, allWorkers.get(newWorker))).start();
				}
			}
		}
	}
}
