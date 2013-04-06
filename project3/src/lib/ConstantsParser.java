package lib;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import communication.WorkerInfo;

/**
 * A helper class to parse the config file and do sanity checks
 * 
 */
public class ConstantsParser implements Serializable{

	// map from worker number of worker info (worker numbers start at 0)
	private HashMap<Integer, WorkerInfo> allWorkers = new HashMap<Integer, WorkerInfo>();
	
	private long recordSize = -1;
	private long chunkSize = -1;
	private long numMappers = -1;
	private long numReducers = -1;

//	public static void main(String[] args) {
//		new ConstantsParser();
//	}

	/**
	 * Constructor that parses the file
	 */
	public ConstantsParser(String filePath) {
		parseConstants(filePath);
	}

	/**
	 * A function that parses the constants file and does sanity checking. It
	 * stores the parsed things in variables
	 */
	private void parseConstants(String filePath) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject o = (JSONObject) parser.parse(new FileReader(filePath));

			recordSize = (Long) o.get("RECORD_SIZE");
			if (recordSize <= 0)
				throw new IllegalArgumentException("Record size <= 0");

			chunkSize = (Long) o.get("CHUNK_SIZE");
			if (chunkSize <= 0)
				throw new IllegalArgumentException("Chunk size <= 0");

			if (chunkSize < recordSize)
				throw new IllegalArgumentException("Chunk size must be at least the record size");
			
			numMappers = (Long) o.get("NUMBER_MAPPERS");
			if (numMappers <= 0)
				throw new IllegalArgumentException("Number of mappers <= 0");

			numReducers = (Long) o.get("NUMBER_REDUCERS");
			if (numReducers <= 0)
				throw new IllegalArgumentException("Number of reducers <= 0");

			// parse all workers
			int workerNum = 0;
			JSONArray workers = (JSONArray) o.get("WORKERS");
			for (Object obj : workers) {
				JSONObject worker = (JSONObject) obj;
				long port = (Long) worker.get("port");
				if (port < 1024 || port > 49151) {
					throw new IllegalArgumentException(
							"Port number must be >= 1024 and <= 49151 (Registered port numbers range)");
				}
				String host = (String) worker.get("host");
				allWorkers.put(workerNum, new WorkerInfo(workerNum, host,
						(int) port));
				workerNum++;
			}


		} catch (FileNotFoundException e1) {
			System.out.println("ERROR: Couldn't find config file (" + filePath + ")");
			e1.printStackTrace();
		} catch (IOException e2) {
			System.out.println("ERROR: JSON file parse error");
			e2.printStackTrace();
		} catch (ParseException e) {
			System.out.println("ERROR: JSON file parse error");
			e.printStackTrace();
		}
	}

	/**
	 * Getter for all workers map
	 */
	public HashMap<Integer, WorkerInfo> getAllWorkers() {
		return allWorkers;
	}

	/**
	 * Getter for record size
	 */
	public long getRecordSize() {
		return recordSize;
	}

	/**
	 * Getter for chunk size
	 */
	public long getChunkSize() {
		return chunkSize;
	}

	/**
	 * Getter for number of mappers
	 */
	public long getNumMappers() {
		return numMappers;
	}

	/**
	 * Getter for number of reducers
	 */
	public long getNumReducers() {
		return numReducers;
	}
}
