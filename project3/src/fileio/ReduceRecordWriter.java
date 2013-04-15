package fileio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import interfaces.Writable;

import lib.Utils;
import lib.KeyValue;
import communication.ReduceTask;

/**
 * This class represents a record writer for the output of reduce.
 *
 */
public class ReduceRecordWriter {

	private RandomAccessFile raf;
	private File f;

	/**
	 * Constructor that opens the output file, and saves the pointer to file
	 * @param task Reduce task this writer is concerned with
	 * @throws FileNotFoundException If the output file is not found
	 */
	public ReduceRecordWriter(ReduceTask task) throws FileNotFoundException {
		raf = new RandomAccessFile(Utils.getReduceOutputFileName(task.reducerInputFileNumber, task.outputDir), "rws");
		f = new File(Utils.getReduceOutputFileName(task.reducerInputFileNumber, task.outputDir));
	}
	
	/**
	 * The write record function. It uses the given delimiter to delimit the key and value
	 * and writes the resulting string to the file.
	 * @param kv Key, value pair to write
	 * @param kvDelimiter Delimiter for key and value
	 * @throws IOException 
	 */
	public void writeRecord(KeyValue<Writable<?>, Writable<?>> kv, String kvDelimiter) throws IOException {
	    long fileLength = f.length();
	    raf.seek(fileLength);
		raf.writeBytes(kv.getKey() + kvDelimiter + kv.getValue()+"\r\n");
	}	
}
