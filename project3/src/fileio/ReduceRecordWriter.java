package fileio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import interfaces.Writable;

import lib.Utils;
import lib.KeyValue;
import communication.ReduceTask;

/**
 * This class represents a record writer for the output of reduce.
 *
 */
public class ReduceRecordWriter {

	/**
	 * A pointer for output writer
	 */
	private PrintWriter outWriter;
	
	/**
	 * Constructor that opens the output file, and saves the pointer to file
	 * @param task Reduce task this writer is concerned with
	 * @throws FileNotFoundException If the output file is not found
	 */
	public ReduceRecordWriter(ReduceTask task) throws FileNotFoundException {
		OutputStream outputFile = new FileOutputStream(Utils.getReduceOutputFileName(task.reducerInputFileNumber, task.outputDir));
		this.outWriter = new PrintWriter(outputFile, true);
	}
	
	/**
	 * The write record function. It uses the given delimiter to delimit the key and value
	 * and writes the resulting string to the file.
	 * @param kv Key, value pair to write
	 * @param kvDelimiter Delimiter for key and value
	 */
	public void writeRecord(KeyValue<Writable<?>, Writable<?>> kv, String kvDelimiter) {
		outWriter.println(kv.getKey() + kvDelimiter + kv.getValue());
		outWriter.flush();
	}
	
}
