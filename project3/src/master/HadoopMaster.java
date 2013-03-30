package master;

import java.io.File;
import java.net.Socket;
import communication.ChunkObject;
import communication.ServiceThread;

import lib.Constants;
import java.util.concurrent.*;

//import nodefunction.RecordReader;

public class HadoopMaster {

	public static Socket[] workerSocket;

	public static ConcurrentLinkedQueue<ChunkObject> chunkQueue;
	public static ConcurrentLinkedQueue<Integer> freeWorkers;

	public static ConcurrentHashMap<ChunkObject, Integer> chunkWorkerMap;
	public static ConcurrentHashMap<Integer, ChunkObject> busyWorkerMap;

	public static final Object OBJ_LOCK = new Object();

	/*
	 * * @param args
	 */
	public static void main(String[] args) {

		chunkQueue = new ConcurrentLinkedQueue<ChunkObject>();
		freeWorkers = new ConcurrentLinkedQueue<Integer>();
		chunkWorkerMap = new ConcurrentHashMap<ChunkObject, Integer>();
		busyWorkerMap = new ConcurrentHashMap<Integer, ChunkObject>();
		// TODO Auto-generated method stub
		System.out.println("File Path = " + args[0]);
		File f = new File(args[0]);
		int fileSize = (int) f.length();
		System.out.println("File Size = " + fileSize);

		int round = (fileSize % lib.Constants.RECORD_SIZE);
		int numRecordsFile = (fileSize / lib.Constants.RECORD_SIZE);
		if (round != 0)
			numRecordsFile++;
		round = (lib.Constants.CHUNK_SIZE % lib.Constants.RECORD_SIZE);
		int numRecordsChunk = lib.Constants.CHUNK_SIZE
				/ lib.Constants.RECORD_SIZE;

		System.out.println(" num of Records per File = " + numRecordsFile);
		System.out.println(" num of Records per Chunk = " + numRecordsChunk);

		int numChunks = numRecordsFile / numRecordsChunk;
		if (round != 0)
			numChunks++;
		System.out.println(" num of Chunks = " + numChunks);

		// Spawn threads for telling workers to map appropriate chunks
		workerSocket = new Socket[Constants.NUMBER_WORKERS];
		for (int i = 0; i < Constants.NUMBER_WORKERS; i++) {
			freeWorkers.add(i);
		}
		// mod chunk numbers with number of workers
		for (int i = 0; i < numChunks; i++) {
			ChunkObject chunKey = new ChunkObject(i, i * numRecordsChunk,
					numRecordsChunk, lib.Constants.RECORD_SIZE, args[0]);
			chunkQueue.add(chunKey);
			chunkWorkerMap.put(chunKey, -1);
		}
		while (!chunkWorkerMap.isEmpty() || !chunkQueue.isEmpty()) {
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

		// System.out.println("Chunk Number in readChunk Call = " + i);
		// RecordReader.readChunk(i, lib.Constants.CHUNK_SIZE,
		// lib.Constants.RECORD_SIZE, args[0]);

	}
}
