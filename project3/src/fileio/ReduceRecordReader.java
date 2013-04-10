package fileio;

import interfaces.Writable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import lib.KeyValue;
import lib.Utils;
import communication.ReduceTask;

/**
 * A class that represents a Record reader for a reducer.
 */
public class ReduceRecordReader {

	private BufferedReader br;
	private Writable<?> keyInstance;
	private Writable<?> valueInstance;
	
	/**
	 * Constructor for record reader. It opens the file for reading, and initializes support
	 * key and value instances
	 * @param task The reduce task this reader concerns
	 * @throws FileNotFoundException If input file is not found
	 * @throws InstantiationException When initializing support key,value
	 * @throws IllegalAccessException When initializing support key,value
	 */
	public ReduceRecordReader(ReduceTask task) throws FileNotFoundException, InstantiationException, IllegalAccessException {		
		File inputFile = new File(Utils.getReduceInputFileName(task.reducerNumber, task.jobName));
		FileInputStream fis;
		fis = new FileInputStream(inputFile);
		this.br = new BufferedReader(new InputStreamReader(fis));
		
		this.keyInstance = (Writable<?>) task.reducerInputKeyClass.newInstance();;
		this.valueInstance = (Writable<?>) task.reducerInputValueClass.newInstance();
	}
	
	/**
	 * The read record function. It reads a single record from the input file
	 * and returns a key value pair from that record. Returns null if there are
	 * no more records to read.
	 * @return Key value pair in the record
	 * @throws IOException If there were problems in reading the record
	 */
	public KeyValue<Writable<?>, Writable<?>> readRecord(String kvDelimiter, String padString) throws IOException {
		String line = br.readLine();
		if (line == null) return null;
		
		String[] lineContents = line.split(kvDelimiter);
		String key = lineContents[0];
		String value = null;
		
		// Remove the pad characters in the value
		if (line.endsWith(padString)){
			value = (lineContents[1].split(padString))[0];
		}
		else
			value = lineContents[1];
		
		this.keyInstance = this.keyInstance.parseFromString(key);
		this.valueInstance = this.valueInstance.parseFromString(value);
		return new KeyValue<Writable<?>, Writable<?>>(keyInstance, valueInstance);
	}
}
