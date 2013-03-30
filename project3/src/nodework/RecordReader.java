package nodework;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import communication.ChunkObject;

import mapper.NaiveMapper;

public class RecordReader {

	private static RandomAccessFile rin;

	/*
	 * Reading a Particular Chunk from a particular file with a given path and
	 * recordSize
	 */
	public static void readChunk(ChunkObject chunk) {
		try {

			rin = new RandomAccessFile(chunk.getFileName(), "r");
			byte[] recordBytes = new byte[chunk.getRecordSize()];
			System.out.println("Doing chunk Number + " + chunk.getChunkNumber() );

			// int readSize = rin.read(b,0,chunkSize);
			// System.out.println("ReadSize = " + readSize);
			for (int i = 0; i < chunk.getNumRecordsChunk(); i++) {
				//System.out.println("Record Number = " + i);
				rin.skipBytes(chunk.getChunkNumber()
						* chunk.getNumRecordsChunk() * chunk.getRecordSize()
						+ chunk.getRecordSize() * i);
				rin.read(recordBytes, 0, chunk.getRecordSize());
				// Check for Encoding Characters
				String value = new String(recordBytes);
				// System.out.println("/*****************************************************************************/\n\n\n");
				// System.out.println(" Key = " + j);
				// System.out.println(" Value we got = \n" + value);
				// System.out.println("/*****************************************************************************/\n\n\n");
				NaiveMapper mapper = new NaiveMapper();
				mapper.map(i, value);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}