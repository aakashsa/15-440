package lib;

public class Constants {
	public static final int RECORD_SIZE = 9;
	public static final int CHUNK_SIZE = 9;
	public static final int NUMBER_MAPPERS = 2;
	public static final int NUMBER_REDUCERS = 2;
	public static final int NUMBER_WORKERS = 2;
	public static final int[] WORKER_PORTS = { 9045, 9056, 9067, 9089 };
	public static final String[] WORKER_HOSTS = { "localhost", "localhost",
			"localhost", "localhost" };
	public static final String fileInputFormat = "KEYVALUEFORMAT";
}
