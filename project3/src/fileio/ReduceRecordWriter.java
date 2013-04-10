package fileio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import interfaces.Writable;

import lib.Utils;
import lib.KeyValue;
import communication.ReduceTask;

public class ReduceRecordWriter {

	private PrintWriter outWriter;
	
	public ReduceRecordWriter(ReduceTask task) throws FileNotFoundException {
		OutputStream outputFile = new FileOutputStream(Utils.getReduceOutputFileName(task.reducerNumber, task.outputDir), true);;
		this.outWriter = new PrintWriter(outputFile, true);
	}
	
	public void writeRecord(KeyValue<Writable<?>, Writable<?>> kv, String kvDelimiter) {
		outWriter.println(kv.getKey() + kvDelimiter + kv.getValue());
		outWriter.flush();
	}
	
}
