package fileio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;

import lib.KeyValue;
import lib.Utils;
import interfaces.Writable;
import interfaces.InputFormat;

import communication.ChunkObject;

/**
 * This class represents a record reader to mapper input.
 *
 */
public class MapRecordReader {

	/**
	 * Pointer to file to read from
	 */
	private RandomAccessFile rin;
	/**
	 * File input format type
	 */
	private Class<?> inputFormatClass;
	private String jobName;
	
	/**
	 * Map records need to be formatted according to input format
	 * @param fileInputFormat Formatting for map input records
	 * @param jobName Job name
	 */
	public MapRecordReader(Class<?> fileInputFormat, String jobName) {
		this.inputFormatClass = fileInputFormat;
		this.jobName = jobName;
	}
	
	/**
	 * Read the records in a given chunk.
	 * @param chunk Chunk to read
	 * @return Iterator over records in chunk
	 * @throws IllegalAccessException If there is a problem in instantiating the input format
	 * @throws InstantiationException If there is a problem in instantiating the input format
	 * @throws IOException If there is an error in reading from file
	 */
	@SuppressWarnings("unchecked")
	public Iterator<KeyValue<Writable<?>, Writable<?>>> readChunk(ChunkObject chunk) throws InstantiationException, IllegalAccessException, IOException {
		ArrayList<KeyValue<Writable<?>, Writable<?>>> records = new ArrayList<KeyValue<Writable<?>, Writable<?>>>();
		
		rin = new RandomAccessFile(chunk.getFileName(), "r");
		byte[] recordBytes = new byte[chunk.getRecordSize()];
		System.out.println(Utils.logInfo(jobName, "Reading Map chunk Number " + chunk.getChunkNumber()));

		int temp = 0;
		for (int i = 0; i < chunk.getNumRecordsChunk(); i++) {
			// Seek to the needed record
			rin.seek(chunk.getChunkNumber() * chunk.getNumRecordsChunk()
					* chunk.getRecordSize() + chunk.getRecordSize() * i);

			temp = rin.read(recordBytes, 0, chunk.getRecordSize());
			if (temp==-1)
				break;

			String value = new String(recordBytes);
			
			InputFormat<Writable<?>, Writable<?>> iFormat = (InputFormat<Writable<?>, Writable<?>>) inputFormatClass.newInstance();
			iFormat.parse(value);
			records.add(new KeyValue<Writable<?>, Writable<?>>(iFormat.getKey(), iFormat.getValue()));
		}
		
		return records.iterator();
	}
	
}
