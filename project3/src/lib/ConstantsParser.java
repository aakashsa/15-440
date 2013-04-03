package lib;

import interfaces.InputFormat;
import interfaces.Writable;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
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
	private Class<?> fileInputFormatClass;
	private String[] mapperReducerTypes = new String[8];
	private Class<?>[] classArray = new Class<?>[8];
	private Class<?> mapperClass;

	public static void main(String[] args) {
		ConstantsParser cp = new ConstantsParser();
	}

	public ConstantsParser() {
		parseConstants();
	}

	/**
	 * A function that parses the constants file and does sanity checking. It
	 * stores the parsed things in variables
	 */
	private void parseConstants() {
		JSONParser parser = new JSONParser();
		try {
			System.out.println("Test Class Path for IntWritable = " + IntWritable.class.getName());
			// URL f =
			// Constants.class.getClassLoader().getResource("lib/Constants.json");
			// System.out.println("File: " + f.getFile());
			// JSONArray a = (JSONArray) parser.parse(new
			// FileReader(f.getFile()));
			JSONObject o = (JSONObject) parser.parse(new FileReader("src/lib/Constants.json"));

			recordSize = (Long) o.get("RECORD_SIZE");
			if (recordSize <= 0)
				throw new IllegalArgumentException("Record size <= 0");

			chunkSize = (Long) o.get("CHUNK_SIZE");
			if (chunkSize <= 0)
				throw new IllegalArgumentException("Chunk size <= 0");

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
							"Port number must be >= 1024 and <= 49151 (Registers port numbers range)");
				}
				String host = (String) worker.get("host");
				allWorkers.put(workerNum, new WorkerInfo(workerNum, host,
						(int) port));
				workerNum++;
			}
			
			// parse mapper and reducer types
			mapperReducerTypes[0] = (String) ((JSONObject) ((JSONObject) o
					.get("TYPES")).get("MAPPER")).get("K1");
			mapperReducerTypes[1] = (String) ((JSONObject) ((JSONObject) o
					.get("TYPES")).get("MAPPER")).get("V1");
			mapperReducerTypes[2] = (String) ((JSONObject) ((JSONObject) o
					.get("TYPES")).get("MAPPER")).get("K2");
			mapperReducerTypes[3] = (String) ((JSONObject) ((JSONObject) o
					.get("TYPES")).get("MAPPER")).get("V2");
			mapperReducerTypes[4] = (String) ((JSONObject) ((JSONObject) o
					.get("TYPES")).get("REDUCER")).get("K1");
			mapperReducerTypes[5] = (String) ((JSONObject) ((JSONObject) o
					.get("TYPES")).get("REDUCER")).get("V1");
			mapperReducerTypes[6] = (String) ((JSONObject) ((JSONObject) o
					.get("TYPES")).get("REDUCER")).get("K2");
			mapperReducerTypes[7] = (String) ((JSONObject) ((JSONObject) o
					.get("TYPES")).get("REDUCER")).get("V2");

			// Sanity checking on mapper and reducer types
			// K2,V2 of Mapper should equal K1,V1 of Reducer
			if (!mapperReducerTypes[2].equals(mapperReducerTypes[4]))
				throw new IllegalArgumentException(
						"Key output of Mapper must equal Key input of Reducer");
			if (!mapperReducerTypes[3].equals(mapperReducerTypes[5]))
				throw new IllegalArgumentException(
						"Value output of Mapper must equal Value input of Reducer");

			// Get Mapper Class
			String mapperClassName = (String) o.get("MAPPERCLASS");
			mapperClass = Class.forName("mapper." + mapperClassName);

			// Check if the types given in constants file are
			// actually the types of the mapper class provided by user
			// Get types of actual Mapper class provided by user
			ParameterizedType pt = (ParameterizedType) mapperClass.getGenericInterfaces()[0];

			String mapK1 = ((Class<?>) pt.getActualTypeArguments()[0]).getName();
			String mapV1 = ((Class<?>) pt.getActualTypeArguments()[1]).getName();
			String mapK2 = ((Class<?>) pt.getActualTypeArguments()[2]).getName();
			String mapV2 = ((Class<?>) pt.getActualTypeArguments()[3]).getName();

			System.out.println(" Mapper Func K1 =  " + mapK1);
			System.out.println(" Mapper Func V1 =  " + mapV1);
			System.out.println(" Mapper Func K2 =  " + mapK2);
			System.out.println(" Mapper Func V2 =  " + mapV2);

			// check if the mapper class has types same as those in constants file
			if (!mapK1.equals("lib." + mapperReducerTypes[0]))
				throw new IllegalArgumentException(
						"Mapper K1 type in Constants.json does not match the type of Mapper class K1");
			if (!mapV1.equals("lib." + mapperReducerTypes[1]))
				throw new IllegalArgumentException(
						"Mapper V1 type in Constants.json does not match the type of Mapper class V1");
			if (!mapK2.equals("lib." + mapperReducerTypes[2]))
				throw new IllegalArgumentException(
						"Mapper K2 type in Constants.json does not match the type of Mapper class K2");
			if (!mapV2.equals("lib." + mapperReducerTypes[3]))
				throw new IllegalArgumentException(
						"Mapper V2 type in Constants.json does not match the type of Mapper class V2");

			// Checking that all of K1,V1,K2,V2 for mapper and reducer have
			// writable types
			// And that they match the given Map function
			for (int i = 0; i < mapperReducerTypes.length; i++) {
				// Should be a Writable
				if (!Writable.validInputFormats().contains(
						mapperReducerTypes[i])) {
					throw new IllegalArgumentException(
							"Key and Value types for mapper and reducer must be Writable types as in Documentation");
				} else {
					// Getting the writable class from type name
					classArray[i] = Class.forName("lib." + mapperReducerTypes[i]);
				}
			}
			for (int i = 0; i < classArray.length; i++) {
				System.out.println(" Class " + i + " " + classArray[i].getName());
			}
			
			// Parse file input format
			String fileInputFormat = (String) o.get("FILE_INPUT_FORMAT");
			
			// check if it's a valid input format
			if (!(InputFormat.validInputFormats().contains(fileInputFormat))) {
				throw new IllegalArgumentException("File input format "
						+ fileInputFormat + " is not supported");
			}
			
			// check if the mapper has the same input key and value types as needed by input format
			fileInputFormatClass = Class.forName("lib." + fileInputFormat);
			InputFormat<?,?> inputFormat = (InputFormat<?,?>) fileInputFormatClass.newInstance();
			
			String keyInput = inputFormat.getKeyType();
			String valueInput = inputFormat.getValueType();
			
			if (!keyInput.equals("lib." + mapperReducerTypes[0]))
				throw new IllegalArgumentException(
						"File input key format (" + keyInput + ") doesn't match with map input key format (lib." + mapperReducerTypes[0] + ")");
			if (!valueInput.equals("lib." + mapperReducerTypes[1]))
				throw new IllegalArgumentException(
						"File input value format (" + keyInput + ") doesn't match with map input value format (lib." + mapperReducerTypes[1] + ")");
			
			System.out.println("Done Parsing");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			System.out.println("ERROR: JSON file parse error");
			e2.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
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
	public Class<?> getInputFormat() {
		return fileInputFormatClass;
	}

	public Class<?> getMapperClass() {
		return mapperClass;
	}
	
	/**
	 * Getters for mapper and reducer types
	 */
	public Class<?> getMapK1Class() {
		return classArray[0];
	}

	public Class<?> getMapV1Class() {
		return classArray[1];
	}

	public Class<?> getMapK2Class() {
		return classArray[2];
	}

	public Class<?> getMapV2Class() {
		return classArray[3];
	}

	public Class<?> getReducerK1Class() {
		return classArray[4];
	}

	public Class<?> getReducerV1Class() {
		return classArray[5];
	}

	public Class<?> getReducerK2Class() {
		return classArray[6];
	}

	public Class<?> getReducerV2Class() {
		return classArray[7];
	}
}
