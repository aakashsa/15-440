package nodework;

import interfaces.InputFormat;
import interfaces.Mapper;
import interfaces.Reducer;
import interfaces.Task;
import interfaces.Writable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import communication.MapTask;
import communication.ReduceTask;
import communication.TaskType;

/**
 * Gets in the port it should listen on from args, and
 * starts listening for task requests on that port. When
 * a request comes in, it checks if it's a map request or
 * a reduce request, and carries it out accordingly.
 */
public class Worker {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// Get port to listen on from command line
		if (args.length != 1) {
			System.out.println("Usage: Worker <port>");
			System.exit(-1);
		}
		int port = 0;
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.out.println("Port must be an integer");
			System.exit(-1);
		}
		if (port < 1024 || port > 49151) {
			System.out.println("Port number must be >= 1024 and <= 49151 (Registered port numbers range)");
			System.exit(-1);
		}
		
		// Setup server socket
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			System.out.println("[INFO] Worker started. Listening for task requests");
			// Start listening for requests on the current port
			while(true) {
				Socket workerSocket = server.accept();
				OutputStream output = workerSocket.getOutputStream();
				InputStream input = workerSocket.getInputStream();
				ObjectOutputStream out = new ObjectOutputStream(output);
				out.flush();
				ObjectInputStream in = new ObjectInputStream(input);
				
				Task obj = (Task) in.readObject();
				
				// Check if task is Map
				if (obj.getTaskType() == TaskType.MAP) {
					MapTask task = (MapTask) obj;
					System.out.println("[INFO] Received map task. Performing map...");
					
					// Use file input format to read records from file
					RecordReader recordReader = new RecordReader(task.fileInputFormat);
					Iterator<InputFormat<Writable<?>, Writable<?>>> itr = recordReader.readChunk(task.chunk);
					// Initialize Mapper instance
					Mapper<Writable<?>, Writable<?>, Writable<?>, Writable<?>> mapper = null;
					try {
						mapper = (Mapper<Writable<?>, Writable<?>, Writable<?>, Writable<?>>) task.mapperClass.newInstance();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					Context<Writable<?>, Writable<?>> cx = new Context<Writable<?>, Writable<?>>();
					
					while (itr.hasNext()) {
						InputFormat<?, ?> iformat = itr.next();
						cx = mapper.map(iformat.getKey(), iformat.getValue(),
								cx);
						if (cx == null) {
							throw new RuntimeException("Mapper returned Null Context");
						}
						ArrayList<KeyValue<Writable<?>, Writable<?>>> toWrite = cx.getAll();
						for (KeyValue<Writable<?>, Writable<?>> kv : toWrite) {
							Partitioner.partitiondata(kv.getKey(), kv.getValue(), task.cp);
						}
						cx.clear();
					}
					System.out.println("[INFO] Finished map task.");
					out.writeObject(new Integer(RecordReader.read));
					out.flush();
				}
				
				// Check if task is Reduce
				else if (obj.getTaskType() == TaskType.REDUCE) {
					ReduceTask task = (ReduceTask) obj;
					System.out.println("[INFO] Received reduce task. Performing reduce...");
					
					File reducerFolder = new File("partition/reducer_" + task.reducerNumber);
					if (reducerFolder != null) {
						File[] listOfFiles = reducerFolder.listFiles();
						
						if (listOfFiles == null) {
							System.out.println("Reducer folder has no files");
						} else {
						
							// Initialize Reducer instance
							Reducer<Writable<?>, Writable<?>, Writable<?>, Writable<?>> reducer = null;
							try {
								reducer = (Reducer<Writable<?>, Writable<?>, Writable<?>, Writable<?>>) task.reducerClass.newInstance();
							} catch (InstantiationException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
							
							// Create new context
							Context<Writable<?>, Writable<?>> cx = new Context<Writable<?>, Writable<?>>();
							
							// Open output file for this reducer
							File outputDir = new File(task.outputDir);
							
							// If the directory does not exist, create it
							if (!outputDir.exists()) { 
								System.out.println("Creating directory: " + outputDir);
								outputDir.mkdir();
							}
							
							OutputStream outputFile = null;
							PrintWriter outWriter = null;
							outputFile = new FileOutputStream(task.outputDir + "part_" + task.reducerNumber + ".txt", true);
							outWriter = new PrintWriter(outputFile, true);
							
							// Call reduce on each key file
							for (File file : listOfFiles) {
								if (file.isFile()) {
									String fileName = file.getName();
									
									// Get key instance
									String[] contents = fileName.split("_");
									String[] keyarray = contents[1].split("\\.");
									String keyName = keyarray[0];
									
									Writable<?> key = null;
									Writable<?> value = null;
									try {
										 key = (Writable<?>) task.reducerInputKeyClass.newInstance();
										 value = (Writable<?>) task.reducerInputValueClass.newInstance();
									} catch (InstantiationException e1) {
										e1.printStackTrace();
									} catch (IllegalAccessException e1) {
										e1.printStackTrace();
									}
									key = key.parseFromString(keyName);
									
									// Go through the key file and collect values in iterator
									Iterator<Writable<?>> valueItr = null;
									ArrayList<Writable<?>> l = new ArrayList<Writable<?>>();
									
									try {
										FileInputStream fis = new FileInputStream("partition/reducer_" + task.reducerNumber + "/" + fileName);
										BufferedReader inputS = new BufferedReader(new InputStreamReader(fis));
										
										String line;
										while ((line = inputS.readLine()) != null) {
											value = value.parseFromString(line);
											l.add(value);
										}
										
										// Call reduce with key and value iterator
										valueItr = l.iterator();
										reducer.reduce(key, valueItr, cx);
										
									} catch (FileNotFoundException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									}
									System.out.println("[INFO] About to write to file...");
									// Write results of reduce to file
									ArrayList<KeyValue<Writable<?>, Writable<?>>> toWrite = cx.getAll();
									System.out.println("[INFO] toWrite.length: " + toWrite.size());
									for (KeyValue<Writable<?>, Writable<?>> kv : toWrite) {
										outWriter.println(kv.getKey() + "\t" + kv.getValue());
									}
									cx.clear();
								}
							}
							
							// Done reducing all keys. Close writers
							outWriter.close();
							outputFile.close();
							
							System.out.println("[INFO] Finished reduce task.");
						}
					}
				}
			}	
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
