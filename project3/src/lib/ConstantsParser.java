package lib;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import communication.WorkerInfo;

/**
 * A helper class to parse the config file and do sanity checks
 */
public class ConstantsParser implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Map from worker number to worker info (worker numbers start at 0)
	 */
	private ConcurrentHashMap<Integer, WorkerInfo> allWorkers = new ConcurrentHashMap<Integer, WorkerInfo>();

	/**
	 * Constructor that parses the file
	 * @param filePath File to parse
	 */
	public ConstantsParser(String filePath) {
		parseConstants(filePath);
	}

	/**
	 * A function that parses the constants file and does sanity checking. It
	 * stores the parsed things in variables
	 * @param filePath File to parse
	 */
	private void parseConstants(String filePath) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject o = (JSONObject) parser.parse(new FileReader(filePath));

			if (o.get("WORKERS") == null)
				throw new IllegalArgumentException("No WORKERS provided");
			
			// parse all workers
			int workerNum = 0;
			JSONArray workers = (JSONArray) o.get("WORKERS");
			for (Object obj : workers) {
				JSONObject worker = (JSONObject) obj;
				if (worker.get("ports") == null)
					throw new IllegalArgumentException("No ports provided for at least one worker");
				
				JSONArray ports = (JSONArray) worker.get("ports");
				
				if (ports.size() <= 0)
					throw new IllegalArgumentException("Each worker must have at least one port");
				
				if (worker.get("numcores") == null)
					throw new IllegalArgumentException("No cores provided for at least one worker");
				
				long numCores = (Long) worker.get("numcores");
				if (numCores <= 0)
					throw new IllegalArgumentException("Num cores <= 0");
				
				if (numCores != ports.size()) {
					throw new IllegalArgumentException("Num ports must be equal to the number of cores");
				}
				if (worker.get("host") == null)
					throw new IllegalArgumentException("No host provided for at least one worker");
				String host = (String) worker.get("host");
				for (Object p : ports) {
					long port = (Long) p;
					if (port < 1024 || port > 49151) {
						throw new IllegalArgumentException("Port number must be >= 1024 and <= 49151 (Registered port numbers range)");
					}
					allWorkers.put(workerNum, new WorkerInfo(workerNum, host, (int) port));
					workerNum++;
				}
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
	public ConcurrentHashMap<Integer, WorkerInfo> getAllWorkers() {
		return allWorkers;
	}

//	/**
//	 * Getter for record size
//	 */
//	public long getRecordSize() {
//		return recordSize;
//	}
//
//	/**
//	 * Getter for chunk size
//	 */
//	public long getChunkSize() {
//		return chunkSize;
//	}
//
//	/**
//	 * Getter for number of reducers
//	 */
//	public long getNumReducers() {
//		return numReducers;
//	}
//	
//	/**
//	 * Getter for mapper output size
//	 */
//	public long getMapperOutputSize() {
//		return mapperOutputSize;
//	}
}
