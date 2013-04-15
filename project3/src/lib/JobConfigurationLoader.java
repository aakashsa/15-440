package lib;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Class Loader for job configuration files
 */
public class JobConfigurationLoader extends ClassLoader {
	
	/**
	 * The get class function. It gets the class object for a given class
	 * @param dir Directory containing the class file
	 * @param className Class name to load
	 * @return Class object
	 * @throws IOException If there was an error in reading the class file
	 */
	public Class<?> getClass(String dir, String className) throws IOException {
		RandomAccessFile f = new RandomAccessFile(dir + className + ".class", "rw");
		byte[] c = new byte[(int)f.length()];
		f.read(c);
		f.close();
		return defineClass(null, c, 0, c.length);
	}	
}
