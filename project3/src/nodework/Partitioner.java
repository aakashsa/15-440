package nodework;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import lib.ConstantsParser;
import lib.Utils;

import interfaces.Writable;

public class Partitioner {

	public static void partitionData(Writable<?> key, Writable<?> value, ConstantsParser cp, String jobName) {
		try {
			int numReducers = (int) cp.getNumReducers();
			int reducerNumber = (key.toString().hashCode() % numReducers);
			if (reducerNumber < 0) {
				reducerNumber += numReducers;
			}

			OutputStream file = new FileOutputStream(Utils.getKeyFileAbsoluteLocation(reducerNumber, jobName, Utils.getKeyFileName(key.toString())), true);
			PrintWriter out = new PrintWriter(file, true);
			out.println(value.toString());
			out.close();
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
