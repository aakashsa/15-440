package lib;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 
 */
public class JobConfigurationLoader extends ClassLoader {
	public Class<?> getClass(String dir, String className) throws IOException {
		RandomAccessFile f = new RandomAccessFile(dir + className + ".class", "rw");
		byte[] c = new byte[(int)f.length()];
		f.read(c);
		f.close();
		return defineClass(null, c, 0, c.length);
	}	
}
