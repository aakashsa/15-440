package master;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import communication.ChunkObject;
import communication.ServiceThread;

import lib.Constants;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class HadoopMaster {

	public static Socket[] workerSocket;

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

		// Parse the JSON config file
		long recordSize = -1;
		long chunkSize = -1;
		long numMappers = -1;
		long numReducers = -1;
		long numWorkers = -1;
		String fileInputFormat = "";
		HashMap<Integer, String> allWorkers = new HashMap<Integer, String>();
		
		JSONParser parser = new JSONParser();
		try {
			//URL f = Constants.class.getClassLoader().getResource("lib/Constants.json");
			//System.out.println("File: " + f.getFile());
			//JSONArray a = (JSONArray) parser.parse(new FileReader(f.getFile()));
			JSONObject o = (JSONObject) parser.parse(new FileReader("/Users/nikhiltibrewal/Desktop/Nikhil/CMU/Junior/Spring 13/15-440/hw1/15-440/project3/bin/lib/Constants.json"));
			
			chunkSize = (Long) o.get("CHUNK_SIZE");
			recordSize = (Long) o.get("RECORD_SIZE");
			numMappers = (Long) o.get("NUMBER_MAPPERS");
			numReducers = (Long) o.get("NUMBER_REDUCERS");
			numWorkers = (Long) o.get("NUMBER_WORKERS");
			fileInputFormat = (String) o.get("FILE_INPUT_FORMAT");
			
			int workerNum = 0;
			JSONArray workers = (JSONArray) o.get("WORKERS");
			for (Object obj : workers) {
				workerNum++;
				JSONObject worker = (JSONObject) obj;
				allWorkers.put(workerNum, worker.get("host") + "," + worker.get("port"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// Get input file name and size
		System.out.println("File Path = " + args[0]);
		File f = new File(args[0]);
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
		workerSocket = new Socket[(int) numWorkers];
		for (int i = 0; i < numWorkers; i++) {
			freeWorkers.add(i);
		}
		// mod chunk numbers with number of workers
		for (int i = 0; i < numChunks; i++) {
			ChunkObject chunKey = new ChunkObject(i, i * numRecordsPerChunk, numRecordsPerChunk, (int) recordSize, args[0]);
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
					new Thread(new ServiceThread(chunkJob, newWorker)).start();
				}
			}
		}
	}
}
