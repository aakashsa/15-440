package master;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import communication.ServiceThread;

import lib.Constants;

//import nodefunction.RecordReader;

public class HadoopMaster {

	public static Socket[] workerSocket;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
		if (round != 0)
			numRecordsChunk++;
		System.out.println(" num of Records per File = " + numRecordsFile);
		System.out.println(" num of Records per Chunk = " + numRecordsChunk);

		round = (numRecordsFile % numRecordsChunk);
		int numChunks = numRecordsFile / numRecordsChunk;
		if (round != 0)
			numChunks++;
		System.out.println(" num of Chunks = " + numChunks);

		// Spawn threads for telling workers to map appropriate chunks
		workerSocket = new Socket[Constants.NUMBER_WORKERS];

		// mod chunk numbers with number of workers
		for (int i = 0; i < numChunks; i++) {
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			new Thread(new ServiceThread(i, i % Constants.NUMBER_WORKERS,
					args[0])).start();
			// System.out.println("Chunk Number in readChunk Call = " + i);
			// RecordReader.readChunk(i, lib.Constants.CHUNK_SIZE,
			// lib.Constants.RECORD_SIZE, args[0]);
		}
	}
}
