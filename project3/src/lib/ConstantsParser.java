package lib;

import interfaces.InputFormat;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import communication.WorkerInfo;

/**
 * A helper class to parse the constants file and do sanity checks
 *
 */
public class ConstantsParser {
	
	// map from worker number of worker info (worker numbers start at 0)
	private HashMap<Integer, WorkerInfo> allWorkers = new HashMap<Integer, WorkerInfo>();
	private long recordSize = -1;
	private long chunkSize = -1;
	private long numMappers = -1;
	private long numReducers = -1;
	private String fileInputFormat = "";
	
//	public static void main(String[] args) {
//	ConstantsParser cp = new ConstantsParser();
//}
	
	public ConstantsParser() {
		parseConstants();
	}
	
	/**
	 * A function that parses the constants file and does sanity checking.
	 * It stores the parsed things in variables
	 */
	private void parseConstants() {
		JSONParser parser = new JSONParser();
		try {
			//URL f = Constants.class.getClassLoader().getResource("lib/Constants.json");
			//System.out.println("File: " + f.getFile());
			//JSONArray a = (JSONArray) parser.parse(new FileReader(f.getFile()));
			JSONObject o = (JSONObject) parser.parse(new FileReader("/Users/nikhiltibrewal/Desktop/Nikhil/CMU/Junior/Spring 13/15-440/hw1/15-440/project3/bin/lib/Constants.json"));
			
			recordSize = (Long) o.get("RECORD_SIZE");
			if (recordSize <= 0) throw new IllegalArgumentException("Record size <= 0");
			
			chunkSize = (Long) o.get("CHUNK_SIZE");
			if (chunkSize <= 0) throw new IllegalArgumentException("Chunk size <= 0");
			
			numMappers = (Long) o.get("NUMBER_MAPPERS");
			if (numMappers <= 0) throw new IllegalArgumentException("Number of mappers <= 0");
			
			numReducers = (Long) o.get("NUMBER_REDUCERS");
			if (numReducers <= 0) throw new IllegalArgumentException("Number of reducers <= 0");
			
			fileInputFormat = (String) o.get("FILE_INPUT_FORMAT");
			if (!(InputFormat.validInputFormats().contains(fileInputFormat))) {
				throw new IllegalArgumentException("File input format " + fileInputFormat + " is not supported");
			}
			
			int workerNum = 0;
			JSONArray workers = (JSONArray) o.get("WORKERS");
			for (Object obj : workers) {
				JSONObject worker = (JSONObject) obj;
				long port = (Integer) worker.get("port");
				if (port < 1024 || port > 49151) {
					throw new IllegalArgumentException("Port number must be >= 1024 and <= 49151 (Registers port numbers range)");
				}
				String host = (String) worker.get("host");
				allWorkers.put(workerNum, new WorkerInfo(workerNum, host, (int) port));
				workerNum++;
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			System.out.println("ERROR: JSON file parse error");
			e2.printStackTrace();
		} catch (ParseException e) {
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
	
	/**
	 * Getter for file input format
	 */
	public String getInputFormat() {
		return fileInputFormat;
	}
}

