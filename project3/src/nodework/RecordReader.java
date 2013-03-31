package nodework;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import communication.ChunkObject;

import mapper.NaiveMapperIntString;

public class RecordReader {

	private static RandomAccessFile rin;
	public static int read = 0;

	/*
	 * Reading a Particular Chunk from a particular file with a given path and
	 * recordSize
	 */
	public static Iterator<String> readChunk(ChunkObject chunk) {
		read = 0;
		ArrayList<String> records = new ArrayList<String>();

		try {

			rin = new RandomAccessFile(chunk.getFileName(), "r");
			byte[] recordBytes = new byte[chunk.getRecordSize()];
			System.out
					.println("Doing chunk Number + " + chunk.getChunkNumber());

			// int readSize = rin.read(b,0,chunkSize);
			// System.out.println("ReadSize = " + readSize);
			for (int i = 0; i < chunk.getNumRecordsChunk(); i++) {
				// System.out.println("Record Number = " + i);
				rin.seek(chunk.getChunkNumber() * chunk.getNumRecordsChunk()
						* chunk.getRecordSize() + chunk.getRecordSize() * i);

				read += rin.read(recordBytes, 0, chunk.getRecordSize());
				// Check for Encoding Characters
				String value = new String(recordBytes);
				records.add(value);

				// System.out.println("/*****************************************************************************/\n\n\n");
				// System.out.println(" Key = " + j);
				// System.out.println(" Value we got = \n" + value);
				// System.out.println("/*****************************************************************************/\n\n\n");
				// NaiveMapper mapper = new NaiveMapper();
				// mapper.map(i, value);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return records.iterator();
	}
}