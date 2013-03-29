package master;

import java.io.File;

import NodeFunction.RecordReader;

public class HadoopMaster {

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

		for (int i = 0; i < numChunks; i++) {
			System.out.println("Chunk Number in readChunk Call = " + i);
			RecordReader.readChunk(i, lib.Constants.CHUNK_SIZE,
					lib.Constants.RECORD_SIZE, args[0]);
		}
	}

}
