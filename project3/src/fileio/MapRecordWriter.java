package fileio;

import interfaces.Writable;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import lib.KeyValue;
import lib.Utils;
import communication.MapTask;

/**
 * A class that represents a record writer for a map operation
 *
 */
public class MapRecordWriter {

	/**
	 * Map task dealing with
	 */
	private MapTask task;
	/**
	 * Output writer pointer
	 */
	private PrintWriter outWriter;

	/**
	 * A constructor that initializes the output file to write records to
	 * @param task Task that this map operation is concerned with
	 * @throws FileNotFoundException If output file is not found
	 */
	public MapRecordWriter(MapTask task) throws FileNotFoundException {
		this.task = task;
		OutputStream outputFile = new FileOutputStream(Utils.getWorkerOutputFileName(task.wi.getWorkerNum(), task.jobName), true);
		this.outWriter = new PrintWriter(outputFile, true);
	}
	
	/**
	 * This method writes a record to the output file. It deals with the
	 * necessary padding to make sure output records are of the expected size.
	 * @param kv Key value to write
	 * @param kvDelimiter Delimiter for the provided key and value
	 * @param padString String to pad any left over bytes of the record with
	 * @throws IllegalArgumentException If the record size is too small for key and value when concatenated with the delimiter
	 */
	public void writeRecord(KeyValue<Writable<?>, Writable<?>> kv, String kvDelimiter, String padString) throws IllegalArgumentException {
		if (padString.length() != 1)
			throw new IllegalArgumentException("Pad string length is not 1 byte");
		
		String record = kv.getKey() + kvDelimiter + kv.getValue();
		
		// Pad the record with a character to make the size equal to record size
		if (record.length() < (this.task.cp.getMapperOutputSize() - 1)) {
			long charsToPad = this.task.cp.getMapperOutputSize() - 1 - record.length(); 
			for (int i = 0; i < charsToPad; i++) {
				record = record + padString;
			}
		} else if (record.length() > (this.task.cp.getMapperOutputSize() - 1)) {
			throw new IllegalArgumentException("Mapper output concatenation of key and value is bigger than mapper output record size");
		}
		outWriter.println(record);
		outWriter.flush();
	}
	
}
