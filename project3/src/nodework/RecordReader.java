package nodework;

import interfaces.InputFormat;
import interfaces.Writable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;

import communication.ChunkObject;

public class RecordReader {

	private static RandomAccessFile rin;
	public static int read = 0;
	private static Class<?> inputFormatClass;

	public RecordReader(Class<?> fileInputFormatClass) {
		inputFormatClass = fileInputFormatClass;
	}
	
	/**
	 * Reading a Particular Chunk from a particular file with a given path and
	 * recordSize
	 */
	public Iterator<InputFormat<Writable<?>, Writable<?>>> readChunk(ChunkObject chunk) {
		read = 0;
		ArrayList<InputFormat<Writable<?>, Writable<?>>> records = new ArrayList<InputFormat<Writable<?>, Writable<?>>>();

		try {
			rin = new RandomAccessFile(chunk.getFileName(), "r");
			byte[] recordBytes = new byte[chunk.getRecordSize()];
			System.out.println("Doing Map chunk Number " + chunk.getChunkNumber());

			for (int i = 0; i < chunk.getNumRecordsChunk(); i++) {
				rin.seek(chunk.getChunkNumber() * chunk.getNumRecordsChunk()
						* chunk.getRecordSize() + chunk.getRecordSize() * i);

				read += rin.read(recordBytes, 0, chunk.getRecordSize());
				// Check for Encoding Characters
				String value = new String(recordBytes);
				@SuppressWarnings("unchecked")
				InputFormat<Writable<?>, Writable<?>> iFormat = (InputFormat<Writable<?>, Writable<?>>) inputFormatClass.newInstance();
				iFormat.parse(value);
				records.add(iFormat);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return records.iterator();
	}
}