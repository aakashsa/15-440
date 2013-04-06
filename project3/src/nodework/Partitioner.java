package nodework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import lib.ConstantsParser;

import interfaces.Writable;

public class Partitioner {

	public static void partitiondata(Writable<?> key, Writable<?> value, ConstantsParser cp) {
		try {
			int numReducers = (int) cp.getNumReducers();
			int reducerNumber = (key.toString().hashCode() % numReducers);
			if (reducerNumber < 0) {
				reducerNumber += numReducers;
			}

			File theDir = new File("partition/reducer_" + reducerNumber);

			// if the directory does not exist, create it
			if (!theDir.exists()) {
				System.out.println("creating directory: " + "reducer_"
						+ reducerNumber);
				theDir.mkdir();
			}

			OutputStream file = new FileOutputStream("partition/reducer_"
					+ reducerNumber + "/key_" + key.toString() + ".txt", true);
			PrintWriter out = new PrintWriter(file, true);
			out.println(value.toString());
			out.close();
			file.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
