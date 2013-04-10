package fileio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import interfaces.Writable;
import interfaces.InputFormat;

import communication.ChunkObject;

/**
 * This class represents a record reader to map input.
 *
 */
public class MapRecordReader {

	private RandomAccessFile rin;
	public int read = 0;
	private Class<?> inputFormatClass;
	
	/**
	 * Map records need to be formatted according to input format
	 * @param fileInputFormat Formatting for map input records
	 */
	public MapRecordReader(Class<?> fileInputFormat) {
		this.inputFormatClass = fileInputFormat;
	}
	
	/**
	 * Read the records in a given chunk and return an iterator over those records
	 * @param chunk Chunk to read
	 * @return iterator over records in chunk
	 */
	@SuppressWarnings("unchecked")
	public Iterator<InputFormat<Writable<?>, Writable<?>>> readChunk(ChunkObject chunk) {
		this.read = 0;
		ArrayList<InputFormat<Writable<?>, Writable<?>>> records = new ArrayList<InputFormat<Writable<?>, Writable<?>>>();
		
		try {
			rin = new RandomAccessFile(chunk.getFileName(), "r");
			byte[] recordBytes = new byte[chunk.getRecordSize()];
			System.out.println("[INFO] Reading Map chunk Number " + chunk.getChunkNumber());

			int temp = 0;
			for (int i = 0; i < chunk.getNumRecordsChunk(); i++) {
				// Seek to the needed record
				rin.seek(chunk.getChunkNumber() * chunk.getNumRecordsChunk()
						* chunk.getRecordSize() + chunk.getRecordSize() * i);

				temp = rin.read(recordBytes, 0, chunk.getRecordSize());
				if (temp==-1)
					break;
				read+= temp;
				// Check for Encoding Characters
				String value = new String(recordBytes);
				
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
