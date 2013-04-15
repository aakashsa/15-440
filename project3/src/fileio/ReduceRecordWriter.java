package fileio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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

	/**
	 * A pointer for output writer
	 */
	private PrintWriter outWriter;
	private RandomAccessFile raf;
	private ReduceTask task;
	private File f;

	/**
	 * Constructor that opens the output file, and saves the pointer to file
	 * @param task Reduce task this writer is concerned with
	 * @throws FileNotFoundException If the output file is not found
	 */
	public ReduceRecordWriter(ReduceTask task) throws FileNotFoundException {
//		OutputStream outputFile = new FileOutputStream(Utils.getReduceOutputFileName(task.reducerInputFileNumber, task.outputDir));
//		this.outWriter = new PrintWriter(outputFile, true);
		raf = new RandomAccessFile(Utils.getReduceOutputFileName(task.reducerInputFileNumber, task.outputDir), "rws");
		f = new File(Utils.getReduceOutputFileName(task.reducerInputFileNumber, task.outputDir));
		this.task = task;
	}
	
	/**
	 * The write record function. It uses the given delimiter to delimit the key and value
	 * and writes the resulting string to the file.
	 * @param kv Key, value pair to write
	 * @param kvDelimiter Delimiter for key and value
	 * @throws IOException 
	 */
	public void writeRecord(KeyValue<Writable<?>, Writable<?>> kv, String kvDelimiter) throws IOException {
//		outWriter.println(kv.getKey() + kvDelimiter + kv.getValue());
//		outWriter.flush();
//	
		System.out.println("YO YO HONEY Reducer Record = " + " " + kv.getKey() + kvDelimiter + kv.getValue()+"\r\n" + Utils.getReduceOutputFileName(task.reducerInputFileNumber, task.outputDir));
	    long fileLength = f.length();
	    raf.seek(fileLength);
		raf.writeBytes(kv.getKey() + kvDelimiter + kv.getValue()+"\r\n");
	}
	
}
